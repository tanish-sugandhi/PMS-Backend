package com.innogent.PMS.service.Impl;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.innogent.PMS.config.S3PathParser;
import com.innogent.PMS.dto.ProgressTrackingDto;
import com.innogent.PMS.entities.ProgressTracking;
import com.innogent.PMS.entities.User;
import com.innogent.PMS.exception.customException.NoSuchUserExistsException;
import com.innogent.PMS.exception.customException.ResourceNotFoundException;
import com.innogent.PMS.mapper.CustomMapper;
import com.innogent.PMS.repository.ProgressTrackingRepository;
import com.innogent.PMS.repository.UserRepository;
import com.innogent.PMS.service.ProgressTrackingService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProgressTrackingImpl implements ProgressTrackingService {
    private static final Logger log = LoggerFactory.getLogger(ProgressTrackingImpl.class);
    @Value("${aws.s3.filePathBaseUrl}")
    private String filePathBaseUrl;
    @Autowired
    ProgressTrackingRepository progressTrackingRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CustomMapper customMapper;

    @Autowired
    private AmazonS3 client;

    @Value("${aws.s3.bucketName}")
    private String bucketName;

    @Override
    public ResponseEntity<?> getById(long id) throws EntityNotFoundException {

        Optional<ProgressTracking> progressTrackingData = Optional.ofNullable(progressTrackingRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Meeting id is not present")));
        //if(progressTrackingData.isPresent())
        //{
        ProgressTrackingDto dto = customMapper.progressEntityToProgressTrackingDto(progressTrackingData.get());
        //if(dto!=null)
        return ResponseEntity.ok(dto);
        //}
        //return ResponseEntity.ok(("Meeting id is not present"));
        //throw new EntityNotFoundException("Meeting id is not present");
    }

    public ResponseEntity<?> getProgressTracking(Integer employeeId) {
        Optional<User> userOptional = userRepository.findById(employeeId);
        if (userOptional.isPresent()) {
            List<ProgressTracking> progressTrackingList = progressTrackingRepository.findAllByUser(userOptional.get());

            List<ProgressTrackingDto> dtoList = progressTrackingList.stream().map(progressTracking -> {
                //long sevenDaysInMillis =  24 * 60 * 60 * 1000;
                long sevenDaysInMillis =60*60 * 1000;
                String notesFile = progressTracking.getNotes().substring(filePathBaseUrl.length());
                GeneratePresignedUrlRequest generatePresignedUrlRequestNotes = new GeneratePresignedUrlRequest(bucketName, notesFile)
                        .withMethod(HttpMethod.GET)
                        .withExpiration(new Date(System.currentTimeMillis() + sevenDaysInMillis));
                URL notesUrl = client.generatePresignedUrl(generatePresignedUrlRequestNotes);

                String recordingFile = progressTracking.getRecording().substring(filePathBaseUrl.length());
                GeneratePresignedUrlRequest generatePresignedUrlRequestRecording = new GeneratePresignedUrlRequest(bucketName, recordingFile)
                        .withMethod(HttpMethod.GET)
                        .withExpiration(new Date(System.currentTimeMillis() + sevenDaysInMillis)); // 7 days expiration
                URL recordingUrl = client.generatePresignedUrl(generatePresignedUrlRequestRecording);
                progressTracking.setNotes(notesUrl.toString());
                progressTracking.setRecording(recordingUrl.toString());
                ProgressTrackingDto dto = customMapper.progressEntityToProgressTrackingDto(progressTracking);
//                dto.setRecording(recordingUrl.toString());
//                dto.setNotes(notesUrl.toString());
                return dto;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(dtoList);
        }
        return ResponseEntity.ok("employee id is not found");
    }

    @Override
    public ResponseEntity<?> editProgressTracking(long meetingId,String title,String month,String year ,@RequestParam MultipartFile notes,@RequestParam MultipartFile recording){
        Optional<ProgressTracking> trackingOpt = progressTrackingRepository.findById(meetingId);
        //ProgressTracking tracking=trackingOpt.get();

        if (trackingOpt.isEmpty()) {
            return ResponseEntity.ok("progresss tracking data is not found");
        }
        ProgressTracking tracking = trackingOpt.get();
        try{
            if(notes != null && !notes.isEmpty())
            {
                deleteFileFromS3(tracking.getNotes());
                String notesFilePath = uploadFileToS3(notes,tracking);
                tracking.setNotes(filePathBaseUrl+""+notesFilePath);
            }
           else{
               tracking.setNotes(tracking.getNotes());
            }

            if(recording!=null && !recording.isEmpty()) {
                deleteFileFromS3(tracking.getRecording());
                String recordingFilePath = uploadFileToS3(recording,tracking);
                tracking.setRecording(filePathBaseUrl+ "" +recordingFilePath);
            }
            else {
                tracking.setRecording(tracking.getRecording());
            }
            if(!title.isEmpty()){
                tracking.setTitle(title);
            }
            if(!month.isEmpty()){
                tracking.setMonth(month);
            }
            if(!year.isEmpty())
            {
                tracking.setYear(year);

            }
//            Integer empId= tracking.getUser().getUserId();
//            Optional<ProgressTracking> existMonthAndYear=progressTrackingRepository.findByUser_UserIdAndMonthAndYear(empId,month,year);
//            if(existMonthAndYear.isPresent())
//            {
//                return ResponseEntity.ok("Month and Year already entered");
//            }
            ProgressTracking savedTracking = progressTrackingRepository.save(tracking);
            ProgressTrackingDto savedTrackingDto = customMapper.progressEntityToProgressTrackingDto(savedTracking);

            return ResponseEntity.ok(savedTrackingDto);
        }catch(Exception e){
            log.error("Error updating progress tracking", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating progress tracking");
        }
    }
    private String uploadFileToS3(MultipartFile file,ProgressTracking tracking) throws IOException
    {
        DateTimeFormatter formatter=DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String currentTime= LocalDateTime.now().format(formatter);
        String fileName = tracking.getUser().getUserId()+"_"+currentTime+"_"+file.getOriginalFilename();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        try {
            client.putObject(new PutObjectRequest(bucketName, fileName, file.getInputStream(), metadata));
        } catch (AmazonServiceException e) {
            log.error("Error uploading file to S3", e);
            throw e;
        }

        return fileName;
    }



    public ResponseEntity<?> getAllData() {
        List<ProgressTracking> getProgressTrackingData = progressTrackingRepository.findAll();
        if (getProgressTrackingData.isEmpty()) {
            return ResponseEntity.ok("unable to find data");
        }
        List<ProgressTrackingDto> progressTrackingDto = customMapper.convertListToDto(getProgressTrackingData);
        return ResponseEntity.ok(progressTrackingDto);
    }


    public ResponseEntity<?> deleteByMeetingId(long id) {
        ProgressTracking progressTrackingOpt = progressTrackingRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("No related data found for Id:"+id,HttpStatus.NOT_FOUND));
        //if (progressTrackingOpt.isPresent()) {
            ProgressTracking progressTrackingData=progressTrackingOpt;
            String notesFilePath=progressTrackingData.getNotes();
            String recordingFilePath=progressTrackingData.getRecording();
            deleteFileFromS3(notesFilePath);
            deleteFileFromS3(recordingFilePath);
            progressTrackingRepository.deleteById(id);
            return ResponseEntity.ok("Data successfully deleted");
        //}
        //return ResponseEntity.ok("No related data found");
    }
    public void deleteFileFromS3(String filePath)
    {
        try {
            String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
            client.deleteObject(new DeleteObjectRequest(bucketName,fileName));
        } catch (AmazonServiceException e) {

            //System.err.println(e.getErrorMessage());

            throw e;
        }
    }
    public ResponseEntity<?> addNotesAndRecording(Integer empId, String title, String month, String year, @RequestParam MultipartFile notes, @RequestParam MultipartFile recording) throws IOException, NoSuchUserExistsException {
     //Optional<ProgressTracking> existMonthAndYearByUser=progressTrackingRepository.findByUser_UserIdAndMonthAndYear(empId,month,year);
     //User existingUser = userRepository.findById(empId).orElseThrow(()->new NoSuchUserExistsException("Invalid user Id provided! No user exist with given user id", HttpStatus.NOT_FOUND));
     Optional<ProgressTracking> existMonthAndYearByUser = progressTrackingRepository.findByUserIdAndMonthAndYearJPQL(empId,month,year);
     if(existMonthAndYearByUser.isPresent())
     {
         return ResponseEntity.ok("Data of"+month+" "+year+" is present");
     }
        DateTimeFormatter formatter=DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String currentTime= LocalDateTime.now().format(formatter);
        String notesFile =empId+"_"+currentTime+"_"+ notes.getOriginalFilename();
        String recordingFile =empId+"_"+currentTime+"_"+recording.getOriginalFilename();

        ObjectMetadata metaDataNotes = new ObjectMetadata();
        metaDataNotes.setContentLength(notes.getSize());
        metaDataNotes.setContentType(notes.getContentType());
         client.putObject(new PutObjectRequest(bucketName, notesFile, notes.getInputStream(), metaDataNotes));

        ObjectMetadata metaDataRecording = new ObjectMetadata();
        metaDataRecording.setContentLength(recording.getSize());
        metaDataRecording.setContentType(recording.getContentType());
         client.putObject(new PutObjectRequest(bucketName, recordingFile, recording.getInputStream(), metaDataRecording));

        ProgressTracking progressTracking = new ProgressTracking();
        progressTracking.setTitle(title);
        progressTracking.setMonth(month);
        progressTracking.setYear(year);
        User user = userRepository.findById(empId).get();
        progressTracking.setUser(user);
        progressTracking.setLineManagerId(user.getManagerId());
       // progressTracking.setNotes(notesUrl.toString());
        progressTracking.setNotes(filePathBaseUrl+notesFile);
        //progressTracking.setRecording(recordingUrl.toString());
        progressTracking.setRecording(filePathBaseUrl+recordingFile);
        ProgressTracking result = progressTrackingRepository.save(progressTracking);

        ProgressTrackingDto dto = customMapper.progressEntityToProgressTrackingDto(result);
        return ResponseEntity.ok(dto);
    }

    @Override
    public Optional<ProgressTracking> findByUserAndDateBetween(User user, LocalDate startDate, LocalDate endDate) {
        return Optional.empty();
    }


    @Override
    public boolean areNotesUploadedForLastMonth(Long managerId) {
        LocalDate now=LocalDate.now();
        LocalDate startOfLastMonth=now.minusMonths(1).with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endOfLastMonth=now.minusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
        Optional<User> manager=userRepository.findById(Math.toIntExact(managerId));
        if(manager.isPresent())
        {
            Optional<ProgressTracking> notes=progressTrackingRepository.findByUserAndDateBetween(manager.get(),startOfLastMonth,endOfLastMonth);
            return notes.isPresent();
        }
        return false;
    }
}


