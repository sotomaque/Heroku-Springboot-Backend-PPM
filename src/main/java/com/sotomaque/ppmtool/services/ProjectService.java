package com.sotomaque.ppmtool.services;

import com.sotomaque.ppmtool.domain.Backlog;
import com.sotomaque.ppmtool.domain.Project;
import com.sotomaque.ppmtool.domain.User;
import com.sotomaque.ppmtool.exceptions.ProjectIdException;
import com.sotomaque.ppmtool.exceptions.ProjectNotFoundException;
import com.sotomaque.ppmtool.repositories.BacklogRepository;
import com.sotomaque.ppmtool.repositories.ProjectRepository;
import com.sotomaque.ppmtool.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private BacklogRepository backlogRepository;

    @Autowired
    private UserRepository userRepository;

    public Project saveOrUpdateProject(Project project, String username) {

        if (project.getId() != null) {
            // try to find project in repository
            Project existingProject = projectRepository.findByProjectIdentifier(project.getProjectIdentifier());
            // if the project exists but you don't own it; throw exception
            if (existingProject != null && (!existingProject.getProjectLeader().equals(username))) {
                throw new ProjectNotFoundException("Project not found in your account");
            } else if (existingProject == null) {
                throw new ProjectNotFoundException("Project with ID: "
                        + project.getProjectIdentifier() +
                        " cannot be updated because it doesn't exist.");
            }
        }

        try {

            User user = userRepository.findByUsername(username);
            project.setUser(user);
            project.setProjectLeader(user.getUsername());
            project.setProjectIdentifier(project.getProjectIdentifier().toUpperCase());

            // only create a new project when we are creating a project, not when we are updating an existing project
            if (project.getId() == null) {
                Backlog backlog = new Backlog();
                // set relationship
                project.setBacklog(backlog);
                backlog.setProject(project);
                // set project id
                backlog.setProjectIdentifier(project.getProjectIdentifier().toUpperCase());
            }

            // if we update a project, we want to return the original backlog not null
            if (project.getId() != null) {
                project.setBacklog(backlogRepository.findByProjectIdentifier(project.getProjectIdentifier().toUpperCase()));
            }

            return projectRepository.save(project);
        } catch (Exception e) {
            throw new ProjectIdException("Project ID: " + project.getProjectIdentifier().toUpperCase() + " already exists.");
        }
    }

    public Project findProjectByIdentifier(String projectId, String username) {
        Project project = projectRepository.findByProjectIdentifier(projectId.toUpperCase());
        if (project == null) {
            throw new ProjectIdException("Project ID: " + projectId.toUpperCase() + " does not exist.");
        }
        // check if user owns that project
        if (!project.getProjectLeader().equals(username)) {
            // if not throw new error
            throw new ProjectNotFoundException("Project not found in your account");
        }
        return project;
    }

    // iterable returns list in json object
    public Iterable<Project> findAllProjects(String username) {
        return projectRepository.findAllByProjectLeader(username);
    }

    public void deleteProjectByIdentifier(String projectId, String username) {
        // try to find the project by id;
        // if found delete it
        // if not throw exceptions in findByProjectIdentifier method
        projectRepository.delete(findProjectByIdentifier(projectId, username));
    }

}
