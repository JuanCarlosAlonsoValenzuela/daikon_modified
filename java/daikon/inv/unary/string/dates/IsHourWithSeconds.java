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

public class IsHourWithSeconds extends SingleString {
    // We are Serializable, so we specify a version to allow changes to
    // method signatures without breaking serialization.  If you add or
    // remove fields, you should change this number to the current date.
    static final long serialVersionUID = 20220423L;

    // Variables starting with dkconfig_ should only be set via the
    // daikon.config.Configuration interface.
    /** Boolean. True iff Positive invariants should be considered. */
    public static boolean dkconfig_enabled = false;

    private static String regex = "^(?:\\d|[01]\\d|2[0-4]):(?:[0-5]\\d):(?:[0-5]\\d)$";

    ///
    /// Required methods
    ///
    private IsHourWithSeconds(PptSlice ppt){ super(ppt); }

    private @Prototype
    IsHourWithSeconds() { super(); }

    private static @Prototype IsHourWithSeconds proto = new @Prototype IsHourWithSeconds();

    // Returns the prototype invariant
    public static @Prototype IsHourWithSeconds get_proto() { return proto; }

    @Override
    public boolean enabled() {
        return dkconfig_enabled;
    }

    @Override
    public IsHourWithSeconds instantiate_dyn(@Prototype IsHourWithSeconds this, PptSlice slice) {
        return new IsHourWithSeconds(slice);
    }

    // A printed representation for user output
    @SideEffectFree
    @Override
    public String format_using(@GuardSatisfied IsHourWithSeconds this, OutputFormat format) {
        if (format == OutputFormat.DAIKON) {
            return var().name() + " is Hour: HH:MM:SS 24-hour format with optional leading 0";
        }

        if (format == OutputFormat.POSTMAN) {
            return "pm.expect(" + getPostmanVariableName(var().name()) + ").to.match(/" + regex + "/)";
        }

        return format_unimplemented(format);

    }

    @Override
    public InvariantStatus check_modified(String v, int count) {
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
        assert other instanceof IsHourWithSeconds;
        return true;
    }

}
