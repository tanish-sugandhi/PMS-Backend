package com.innogent.PMS.service.Impl;

import com.innogent.PMS.entities.Stage;
import com.innogent.PMS.repository.GoalRepository;
import com.innogent.PMS.repository.StageRepository;
import com.innogent.PMS.repository.UserRepository;
import com.innogent.PMS.service.StageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StageServiceImpl implements StageService {
    @Autowired
    private StageRepository stageRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GoalRepository goalRepository;

    @Override
    public Stage setStage(Stage stage) {
         return stageRepository.save(stage);
    }
}
