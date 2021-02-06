package org.harry.jesus.jesajautils.judaerrmsg;

import org.harry.jesus.jesajautils.HTMLRendering;

import static org.apache.commons.text.StringEscapeUtils.escapeHtml4;

public class BibleStudyException extends Exception {

    private final Code theCode;
    private final Object [] params;
    private Throwable cause = null;

    /**
     * Internal Exception
     * @param message the message
     */
    public BibleStudyException(String message) {
        this(Code.INTERNAL_ERROR, message);
    }

    /**
     * Internal Exception with code
     * @param message the message
     * @param cause the cause
     */
    public BibleStudyException(String message, Throwable cause) {
        this(Code.INTERNAL_ERROR, cause, message);
    }

    /**
     * Exception with code and message
     * @param code the exception code
     * @param params the params for the format message
     */
    public BibleStudyException(Code code, Object ... params) {
        super();
        this.theCode = code;
        this.params = params;
    }

    /**
     * Exception with cause
     * @param code exception code
     * @param cause the error cause
     * @param params the parameters for the format string
     */
    public BibleStudyException(Code code, Throwable cause, Object ... params) {
        super();
        this.theCode = code;
        this.params = params;
        this.cause = cause;
    }

    @Override
    public String getMessage() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("Exception code: ");
        buffer.append(this.theCode.getTheCode());
        buffer.append("\n");
        String msg = String.format(theCode.getFmtCode(), this.params);
        buffer.append(msg);
        if (this.getCause() != null) {
            buffer.append("\nCause: Class-Name:").append(getCause().getClass().getName());
            buffer.append("Message:\n").append(getCause().getMessage());
            StackTraceElement[] elements = getCause().getStackTrace();
            HTMLRendering.putStackTraceToBuffer(buffer, elements, false);

        }
        return buffer.toString();
    }

    public String getMessageHTML() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(escapeHtml4("Exception code: "));
        buffer.append(this.theCode.getTheCode());
        buffer.append("<br>");
        String msg = String.format(theCode.getFmtCode(), this.params);
        buffer.append(msg);
        if (this.getCause() != null) {
            buffer.append("<br>Cause: Class-Name:").append(escapeHtml4(getCause().getClass().getName()));
            buffer.append("Message: <br>").append(escapeHtml4(getCause().getMessage()));
            StackTraceElement[] elements = getCause().getStackTrace();
            HTMLRendering.putStackTraceToBuffer(buffer, elements, true);

        }
        return buffer.toString();
    }

    @Override
    public Throwable getCause() {
        return this.cause;
    }

    public enum Code {
        BIBLE_NOT_FOUND(1, "Bible with Id %s not found"),
        BOOK_NOT_FOUND(2, "Book with number %d not found in bible %s"),
        CHAPTER_NOT_FOUND(3, "Chapter with number %d not found in book %d"),
        VERSE_NOT_FOUND(4, "Verse with number %d not found in chapter %d"),
        /**
         * fill in here
         */
        INTERNAL_ERROR(500, "Code: %s")
        ;

        private final Integer theCode;

        private final String fmtCode;

        Code(Integer theCode, String fmtCode) {
            this.theCode = theCode;
            this.fmtCode = fmtCode;
        }

        public Integer getTheCode() {
            return theCode;
        }

        public String getFmtCode() {
            return fmtCode;
        }
    }
}
