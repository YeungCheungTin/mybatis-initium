package org.mybatis.generator.exception;

/**
 * This class is used by the ShellCallback methods to denote unrecoverable
 * errors.
 *
 * @author Jeff Butler
 */
public class ShellException extends Exception {
    static final long serialVersionUID = -2026841561754434544L;

    /**
     *
     */
    public ShellException() {
        super();
    }

    /**
     *
     */
    public ShellException(String arg0) {
        super(arg0);
    }

    /**
     *
     */
    public ShellException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    /**
     *
     */
    public ShellException(Throwable arg0) {
        super(arg0);
    }
}
