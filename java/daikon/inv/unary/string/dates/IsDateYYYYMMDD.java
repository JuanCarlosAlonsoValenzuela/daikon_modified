package daikon.inv.unary.string.dates;

import daikon.PptSlice;
import daikon.inv.Invariant;
import daikon.inv.InvariantStatus;
import daikon.inv.OutputFormat;
import daikon.inv.unary.string.SingleString;
import org.checkerframework.checker.lock.qual.GuardSatisfied;
import org.checkerframework.dataflow.qual.Pure;
import org.checkerframework.dataflow.qual.SideEffectFree;
import typequals.prototype.qual.Prototype;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static daikon.agora.PostmanUtils.getPostmanVariableName;

public class IsDateYYYYMMDD extends SingleString {
    // We are Serializable, so we specify a version to allow changes to
    // method signatures without breaking serialization.  If you add or
    // remove fields, you should change this number to the current date.
    static final long serialVersionUID = 20220420L;

    // Variables starting with dkconfig_ should only be set via the
    // daikon.config.Configuration interface.
    /** Boolean. True iff Positive invariants should be considered. */
    public static boolean dkconfig_enabled = false;

    private static final String regex = "^(?:19\\d{2}|20[01234][0-9]|2050|9999)[-/.](?:0[1-9]|1[012])[-/.](?:0[1-9]|[12][0-9]|3[01])$";

    ///
    /// Required methods
    ///
    private IsDateYYYYMMDD(PptSlice ppt){ super(ppt); }

    private @Prototype
    IsDateYYYYMMDD() { super(); }

    private static @Prototype IsDateYYYYMMDD proto = new @Prototype IsDateYYYYMMDD();

    // Returns the prototype invariant
    public static @Prototype IsDateYYYYMMDD get_proto() { return proto; }

    @Override
    public boolean enabled() {
        return dkconfig_enabled;
    }

    @Override
    public IsDateYYYYMMDD instantiate_dyn(@Prototype IsDateYYYYMMDD this, PptSlice slice) {
        return new IsDateYYYYMMDD(slice);
    }

    // A printed representation for user output
    @SideEffectFree
    @Override
    public String format_using(@GuardSatisfied IsDateYYYYMMDD this, OutputFormat format) {
        if (format == OutputFormat.DAIKON) {
            return var().name() + " is a Date. Format: YYYY/MM/DD";
        }

        if (format == OutputFormat.POSTMAN) {
            return "pm.expect(" + getPostmanVariableName(var().name()) + ").to.match(/" + regex + "/)";
        }

        return format_unimplemented(format);

    }

    @Override
    public InvariantStatus check_modified(String v, int count) {
        /*
        *   The regex matches on a date with the YYYY/MM/DD format (Year min: 1900, Year max: 2050).
        *   For example:
        *       - 1900/12/01
        *       - 2019.01.25
        *       - 2050-10-30
        */
        // ^(?:19\d{2}|20[01234][0-9]|2050)[-/.](?:0[1-9]|1[012])[-/.](?:0[1-9]|[12][0-9]|3[01])$
        Pattern pattern = Pattern.compile(regex);

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
        assert other instanceof IsDateYYYYMMDD;
        return true;
    }

}
