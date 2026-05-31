package com.snjdigitalsolutions.lablensfx.orm.model;

import com.snjdigitalsolutions.lablensfx.orm.FileSystemObject;
import javafx.beans.property.*;

import java.time.Instant;

public class FileSystemObjectModel {

    private final ObjectProperty<Instant> modifiedTime = new SimpleObjectProperty<>();
    private final StringProperty permission = new SimpleStringProperty();
    private final StringProperty fileType = new SimpleStringProperty();
    private final StringProperty parentPath = new SimpleStringProperty();
    private final StringProperty fileName = new SimpleStringProperty();
    private final LongProperty fileSize = new SimpleLongProperty();
    private final LongProperty computeResourceID = new SimpleLongProperty();
    private final BooleanProperty trackFile = new SimpleBooleanProperty(false);
    private final BooleanProperty dbIsSource = new SimpleBooleanProperty(false);
    private final BooleanProperty nonExistantFile = new SimpleBooleanProperty(false);

    /**
     * Creates an empty file-system object model.
     */
    public FileSystemObjectModel() {}

    /**
     * Creates a model populated from the given entity, with {@code dbIsSource} defaulting to {@code false}.
     *
     * @param fileSystemObject the source entity
     */
    public FileSystemObjectModel(FileSystemObject fileSystemObject) {
        fromFileSystemObject(fileSystemObject);
    }

    /**
     * Creates a model populated from the given entity with an explicit non-existent-file flag.
     *
     * @param fileSystemObject the source entity
     * @param nonExistantFile  {@code true} if the file no longer exists on the remote host
     */
    public FileSystemObjectModel(FileSystemObject fileSystemObject, boolean nonExistantFile) {
        fromFileSystemObject(fileSystemObject);
        this.nonExistantFile.set(nonExistantFile);
    }

    /**
     * Populates this model's fields from the given {@link FileSystemObject} entity.
     *
     * @param fileSystemObject the source entity
     */
    public void fromFileSystemObject(FileSystemObject fileSystemObject) {
        modifiedTime.set(fileSystemObject.getModifiedTime());
        permission.set(String.valueOf(fileSystemObject.getPermission()));
        fileType.set(fileSystemObject.getFileType());
        parentPath.set(fileSystemObject.getParentPath());
        fileName.set(fileSystemObject.getFileName());
        fileSize.set(fileSystemObject.getFileSize());
        trackFile.set(fileSystemObject.isTrackFile());
        dbIsSource.setValue(true);
        computeResourceID.setValue(fileSystemObject.getComputeResource().getId());
    }

    /**
     * Converts this model to a {@link FileSystemObject} entity.
     *
     * @return a new entity populated from this model's current values
     */
    public FileSystemObject toFileSystemObject() {
        FileSystemObject fileSystemObject = new FileSystemObject();
        fileSystemObject.setModifiedTime(modifiedTime.get());
        if (permission.get() != null) {
            fileSystemObject.setPermission(Integer.parseInt(permission.get()));
        } else {
            fileSystemObject.setPermission(0);
        }
        fileSystemObject.setFileType(fileType.get());
        fileSystemObject.setParentPath(parentPath.get());
        fileSystemObject.setFileName(fileName.get());
        fileSystemObject.setFileSize(fileSize.get());
        fileSystemObject.setTrackFile(trackFile.get());
        return fileSystemObject;
    }

    /** @return the last-modified timestamp of this file */
    public Instant getModifiedTime() { return modifiedTime.get(); }
    /** @param modifiedTime the modification time to set */
    public void setModifiedTime(Instant modifiedTime) { this.modifiedTime.set(modifiedTime); }
    /** @return the observable property for the last-modified timestamp */
    public ObjectProperty<Instant> modifiedTimeProperty() { return modifiedTime; }

    /** @return the permission string for this file (e.g. {@code "-rw-r--r--"}) */
    public String getPermission() { return permission.get(); }
    /** @param permission the permission string to set */
    public void setPermission(String permission) { this.permission.set(permission); }
    /** @return the observable property for the permission string */
    public StringProperty permissionProperty() { return permission; }

    /** @return the file type character (e.g. {@code "f"} for regular file, {@code "d"} for directory) */
    public String getFileType() { return fileType.get(); }
    /** @param fileType the file type to set */
    public void setFileType(String fileType) { this.fileType.set(fileType); }
    /** @return the observable property for the file type */
    public StringProperty fileTypeProperty() { return fileType; }

    /** @return the parent directory path of this file */
    public String getParentPath() { return parentPath.get(); }
    /** @param parentPath the parent path to set */
    public void setParentPath(String parentPath) { this.parentPath.set(parentPath); }
    /** @return the observable property for the parent path */
    public StringProperty parentPathProperty() { return parentPath; }

    /** @return the name of this file (without the parent path) */
    public String getFileName() { return fileName.get(); }
    /** @param fileName the file name to set */
    public void setFileName(String fileName) { this.fileName.set(fileName); }
    /** @return the observable property for the file name */
    public StringProperty fileNameProperty() { return fileName; }

    /** @return the size of this file in bytes */
    public long getFileSize() { return fileSize.get(); }
    /** @param fileSize the file size to set */
    public void setFileSize(long fileSize) { this.fileSize.set(fileSize); }
    /** @return the observable property for the file size */
    public LongProperty fileSizeProperty() { return fileSize; }

    /**
     * Returns whether this file is being tracked for change monitoring.
     *
     * @return {@code true} if the file is tracked
     */
    public boolean isTrackFile() {
        return trackFile.get();
    }

    /**
     * Sets whether this file should be tracked for change monitoring.
     *
     * @param trackFile {@code true} to track the file
     */
    public void setTrackFile(boolean trackFile) {
        this.trackFile.set(trackFile);
    }

    /**
     * Returns the observable property for the track-file flag.
     *
     * @return the track-file property
     */
    public BooleanProperty trackFileProperty() {
        return trackFile;
    }

    /**
     * Returns whether this model's data was loaded from the database rather than a live scan.
     *
     * @return {@code true} if the data source is the database
     */
    public boolean isDbIsSource() {
        return dbIsSource.get();
    }

    /**
     * Returns the observable property indicating whether data came from the database.
     *
     * @return the db-is-source property
     */
    public BooleanProperty dbIsSourceProperty() {
        return dbIsSource;
    }

    /**
     * Returns the ID of the compute resource this file belongs to.
     *
     * @return the compute resource ID
     */
    public long getComputeResourceID() {
        return computeResourceID.get();
    }

    /**
     * Returns the observable property for the compute resource ID.
     *
     * @return the compute resource ID property
     */
    public LongProperty computeResourceIDProperty() {
        return computeResourceID;
    }

    /**
     * Returns whether this file no longer exists on the remote host.
     *
     * @return {@code true} if the file is non-existent
     */
    public boolean isNonExistantFile() {
        return nonExistantFile.get();
    }

    /**
     * Returns the observable property indicating whether this file no longer exists on the host.
     *
     * @return the non-existent-file property
     */
    public BooleanProperty nonExistantFileProperty() {
        return nonExistantFile;
    }
}
