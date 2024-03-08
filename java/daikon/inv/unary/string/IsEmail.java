package daikon.inv.unary.string;

import daikon.PptSlice;
import daikon.inv.Invariant;
import daikon.inv.InvariantStatus;
import daikon.inv.OutputFormat;
import org.checkerframework.checker.lock.qual.GuardSatisfied;
import org.checkerframework.dataflow.qual.Pure;
import org.checkerframework.dataflow.qual.SideEffectFree;
import typequals.prototype.qual.Prototype;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IsEmail extends SingleString {
    // We are Serializable, so we specify a version to allow changes to
    // method signatures without breaking serialization.  If you add or
    // remove fields, you should change this number to the current date.
    static final long serialVersionUID = 20220420L;

    // Variables starting with dkconfig_ should only be set via the
    // daikon.config.Configuration interface.
    /** Boolean. True iff Positive invariants should be considered. */
    public static boolean dkconfig_enabled = false;

    ///
    /// Required methods
    ///
    private IsEmail(PptSlice ppt){ super(ppt); }

    private @Prototype
    IsEmail() { super(); }

    private static @Prototype IsEmail proto = new @Prototype IsEmail();

    // Returns the prototype invariant
    public static @Prototype IsEmail get_proto() { return proto; }

    @Override
    public boolean enabled() {
        return dkconfig_enabled;
    }

    @Override
    public IsEmail instantiate_dyn(@Prototype IsEmail this, PptSlice slice) {
        return new IsEmail(slice);
    }

    // A printed representation for user output
    @SideEffectFree
    @Override
    public String format_using(@GuardSatisfied IsEmail this, OutputFormat format) {
        if (format == OutputFormat.DAIKON) {
            return var().name() + " is Email";
        }

        if (format == OutputFormat.POSTMAN) {
            return "TODO: IMPLEMENT POSTMAN ASSERTION";
        }

        return format_unimplemented(format);

    }

    @Override
    public InvariantStatus check_modified(String v, int count) {

        Pattern pattern = Pattern.compile("^(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9]^[0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])$");

        Matcher matcher = pattern.matcher(v);

        if (matcher.matches()) {
            return InvariantStatus.NO_CHANGE;
        }
        return InvariantStatus.FALSIFIED;
    }

    @Override
    public InvariantStatus add_modified(String v, int count) { return check_modified(v, count); }

    @Override
    protected  double computeConfidence() {
        return 1 - Math.pow(.1, ppt.num_samples());
    }

    @Pure
    @Override
    public boolean isSameFormula(Invariant other) {
        assert other instanceof IsEmail;
        return true;
    }

}
