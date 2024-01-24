package life.qbic.xmledit;

public class FeedbackStore {
    private String filePath = null;
    private boolean valid = false;
    private String validationError = null;
    private boolean exported =  false;
    private String exportError = null;

    FeedbackStore(String filePath) {
        this.filePath = filePath;

    }
    // getter and setter methods for the file path
    public String getFilePath() {
        return filePath;
    }
    // getter and setter methods for the validation status
    public boolean getValidity() {
        return valid;
    }
    public void setValidity(boolean valid) {
        this.valid = valid;
    }
    // getter and setter methods for the validation error
    public String getValidationError() {
        return validationError;
    }
    public void setValidationError(String validationError) {
        this.validationError = validationError;
    }
    // getter and setter methods for the export status
    public boolean getExported() {
        return exported;
    }
    public void setExported(boolean exported) {
        this.exported = exported;
    }
    // getter and setter methods for the export error
    public String getExportError() {
        return exportError;
    }
    public void setExportError(String exportError) {
        this.exportError = exportError;
    }



}
