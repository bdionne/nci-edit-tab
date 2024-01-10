package gov.nih.nci.utils;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;

import edu.stanford.protege.metaproject.ConfigurationManager;
import edu.stanford.protege.metaproject.api.Description;
import edu.stanford.protege.metaproject.api.Name;
import edu.stanford.protege.metaproject.api.PolicyFactory;
import edu.stanford.protege.metaproject.api.Project;
import edu.stanford.protege.metaproject.api.ProjectId;
import edu.stanford.protege.metaproject.api.ProjectOptions;
import edu.stanford.protege.metaproject.api.ServerConfiguration;
import edu.stanford.protege.metaproject.api.UserId;
import edu.stanford.protege.metaproject.api.exception.IdAlreadyInUseException;
import edu.stanford.protege.metaproject.impl.ConfigurationBuilder;

public class ProjectOptionsBuilder {

	private static final Logger logger = LoggerFactory.getLogger(ProjectOptionsBuilder.class.getName());
	private Project project;
	private PolicyFactory factory = ConfigurationManager.getFactory();
	
	/**
     * No-arguments constructor
     */
    public ProjectOptionsBuilder() { }
    
    /* projects */
    public ProjectOptionsBuilder setProject(Project proj) {
    	this.project = proj;
    	return this;
    }
    /**
     * Set the name of the project
     *
     * @param projectId   Project identifier
     * @param projectName   New project name
     * @return ProjectOptionsBuilder
     */
    public ProjectOptionsBuilder setProjectName(ProjectId projectId, Name projectName) {
        checkNotNull(projectId);
        checkNotNull(projectName);
        Optional<Project> projectOpt = getProject(projectId);
        if (projectOpt.isPresent()) {
        	Project proj = projectOpt.get();
            setProject(factory.getProject(proj.getId(), proj.namespace(), projectName, proj.getDescription(), proj.getOwner(), proj.getOptions()));
        }
        return this;
    }

    /**
     * Change the description of the given project
     *
     * @param projectId   Project identifier
     * @param projectDescription    New project description
     * @return ProjectOptionsBuilder
     */
    public ProjectOptionsBuilder setProjectDescription(ProjectId projectId, Description projectDescription) {
        checkNotNull(projectId);
        checkNotNull(projectDescription);
        Optional<Project> projectOpt = getProject(projectId);
        if (projectOpt.isPresent()) {
            Project proj = projectOpt.get();
            setProject(factory.getProject(proj.getId(), proj.namespace(), proj.getName(), projectDescription, proj.getOwner(), proj.getOptions()));
        }
        return this;
    }

    /**
     * Change the owner of the specified project
     *
     * @param projectId   Project identifier
     * @param userId    New owner user identifier
     * @return ProjectOptionsBuilder
     */
    public ProjectOptionsBuilder setProjectOwner(ProjectId projectId, UserId userId) {
        checkNotNull(projectId);
        checkNotNull(userId);
        Optional<Project> projectOpt = getProject(projectId);
        if (projectOpt.isPresent()) {
            Project proj = projectOpt.get();
            setProject(factory.getProject(proj.getId(), proj.namespace(), proj.getName(), proj.getDescription(), userId, proj.getOptions()));
        }
        return this;
    }

    /**
     * Change the file location of the specified project
     *
     * @param projectId Project identifier
     * @param file   Project file
     * @return ProjectOptionsBuilder
     */
    /*public ProjectOptionsBuilder setProjectFile(ProjectId projectId, File file) {
        checkNotNull(projectId);
        checkNotNull(file);
        Optional<Project> projectOpt = getProject(projectId);
        if (projectOpt.isPresent()) {
            Project proj = projectOpt.get();
            setProject(factory.getProject(proj.getId(), proj.namespace(), proj.getName(), proj.getDescription(), proj.getOwner(), proj.getOptions()));
        }
        return this;
    }*/

    /**
     * Set the options for this project
     *
     * @param projectId Project identifier
     * @param projectOptions    Project options
     * @return ProjectOptionsBuilder
     */
    public ProjectOptionsBuilder setProjectOptions(ProjectId projectId, ProjectOptions projectOptions) {
        checkNotNull(projectId);
        checkNotNull(projectOptions);
        Optional<Project> projectOpt = getProject(projectId);
        if (projectOpt.isPresent()) {
            Project proj = projectOpt.get();
            setProject(factory.getProject(projectId, proj.namespace(), proj.getName(), proj.getDescription(), proj.getOwner(), Optional.of(projectOptions)));
        }
        return this;
    }

    private Optional<Project> getProject(ProjectId projectId) {
        
        if(project.getId().equals(projectId)) {
            return Optional.of(project);
        }
        return Optional.absent();
    }
}
