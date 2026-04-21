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

    public FileSystemObjectModel() {}

    public FileSystemObjectModel(FileSystemObject fileSystemObject) {
        fromFileSystemObject(fileSystemObject);
    }

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

    public Instant getModifiedTime() { return modifiedTime.get(); }
    public void setModifiedTime(Instant modifiedTime) { this.modifiedTime.set(modifiedTime); }
    public ObjectProperty<Instant> modifiedTimeProperty() { return modifiedTime; }

    public String getPermission() { return permission.get(); }
    public void setPermission(String permission) { this.permission.set(permission); }
    public StringProperty permissionProperty() { return permission; }

    public String getFileType() { return fileType.get(); }
    public void setFileType(String fileType) { this.fileType.set(fileType); }
    public StringProperty fileTypeProperty() { return fileType; }

    public String getParentPath() { return parentPath.get(); }
    public void setParentPath(String parentPath) { this.parentPath.set(parentPath); }
    public StringProperty parentPathProperty() { return parentPath; }

    public String getFileName() { return fileName.get(); }
    public void setFileName(String fileName) { this.fileName.set(fileName); }
    public StringProperty fileNameProperty() { return fileName; }

    public long getFileSize() { return fileSize.get(); }
    public void setFileSize(long fileSize) { this.fileSize.set(fileSize); }
    public LongProperty fileSizeProperty() { return fileSize; }

    public boolean isTrackFile() {
        return trackFile.get();
    }

    public void setTrackFile(boolean trackFile) {
        this.trackFile.set(trackFile);
    }

    public BooleanProperty trackFileProperty() {
        return trackFile;
    }

    public boolean isDbIsSource() {
        return dbIsSource.get();
    }

    public BooleanProperty dbIsSourceProperty() {
        return dbIsSource;
    }

    public long getComputeResourceID() {
        return computeResourceID.get();
    }

    public LongProperty computeResourceIDProperty() {
        return computeResourceID;
    }
}
