package com.dhaval.personal.playground.services;

import com.dhaval.personal.playground.entity.Job;
import com.dhaval.personal.playground.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JobService {

    @Autowired
    private JobRepository jobRepository;

    public void saveJob(Job job) {
        // Save the job entity to the database
        System.out.println("Saving job with ID: " + job.getId() + " and status: " + job.getStatus());
        jobRepository.save(job);
    }
}