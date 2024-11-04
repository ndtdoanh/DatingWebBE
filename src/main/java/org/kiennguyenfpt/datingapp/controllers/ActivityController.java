package org.kiennguyenfpt.datingapp.controllers;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/activities")
@CrossOrigin
public class ActivityController {
    /*
    private final ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    /*
    @GetMapping("/user")
    public ResponseEntity<CommonResponse<List<Activity>>> getUserActivities(@RequestParam Long userId) {
        CommonResponse<List<Activity>> response = new CommonResponse<>();
        try {
            List<Activity> activities = activityService.getUserActivities(userId);
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Activities retrieved successfully.");
            response.setData(activities);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Error retrieving activities: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
     */

}
