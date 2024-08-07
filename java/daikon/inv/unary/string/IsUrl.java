package daikon.inv.unary.string;

import daikon.PptSlice;
import daikon.inv.Invariant;
import daikon.inv.InvariantStatus;
import daikon.inv.OutputFormat;
import daikon.inv.unary.scalar.Positive;
import org.checkerframework.checker.lock.qual.GuardSatisfied;
import org.checkerframework.dataflow.qual.Pure;
import org.checkerframework.dataflow.qual.SideEffectFree;
import typequals.prototype.qual.Prototype;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static daikon.agora.PostmanUtils.getPostmanVariableName;

public class IsUrl extends SingleString {
    // We are Serializable, so we specify a version to allow changes to
    // method signatures without breaking serialization.  If you add or
    // remove fields, you should change this number to the current date.
    static final long serialVersionUID = 20220228L;

    // Variables starting with dkconfig_ should only be set via the
    // daikon.config.Configuration interface.
    /** Boolean. True iff Positive invariants should be considered. */
    public static boolean dkconfig_enabled = false;

    private static String regex = "^(?:(?:https?|ftp)://)(?:\\S+(?::\\S*)?@)?(?:(?!10(?:\\.\\d{1,3}){3})(?!127(?:\\.\\d{1,3}){3})(?!169\\.254(?:\\.\\d{1,3}){2})(?!192\\.168(?:\\.\\d{1,3}){2})(?!172\\.(?:1[6-9]|2\\d|3[0-1])(?:\\.\\d{1,3}){2})(?:[1-9]\\d?|1\\d\\d|2[01]\\d|22[0-3])(?:\\.(?:1?\\d{1,2}|2[0-4]\\d|25[0-5])){2}(?:\\.(?:[1-9]\\d?|1\\d\\d|2[0-4]\\d|25[0-4]))|(?:(?:[\\w\\x{00a1}-\\x{ffff}0-9]+-?)*[\\w\\x{00a1}-\\x{ffff}0-9]+)(?:\\.(?:[\\w\\x{00a1}-\\x{ffff}0-9]+-)*[\\w\\x{00a1}-\\x{ffff}0-9]+)*(?:\\.(?:[a-zA-Z\\x{00a1}-\\x{ffff}]{2,})))(?::\\d{2,5})?(?:/[^\\s]*)?$";

    // This regular expression was modified to be compatible with Postman. Two changes:
    // 1- Replace "/" with "\\/"
    // 2- Replace  "\x{00a1}-\x{ffff}" (range of unicode characters) with "\\u00a1-\\uffff"
    private static String regexPostman = "^(?:(?:https?|ftp):\\/\\/)(?:\\S+(?::\\S*)?@)?(?:(?!10(?:\\.\\d{1,3}){3})(?!127(?:\\.\\d{1,3}){3})(?!169\\.254(?:\\.\\d{1,3}){2})(?!192\\.168(?:\\.\\d{1,3}){2})(?!172\\.(?:1[6-9]|2\\d|3[0-1])(?:\\.\\d{1,3}){2})(?:[1-9]\\d?|1\\d\\d|2[01]\\d|22[0-3])(?:\\.(?:1?\\d{1,2}|2[0-4]\\d|25[0-5])){2}(?:\\.(?:[1-9]\\d?|1\\d\\d|2[0-4]\\d|25[0-4]))|(?:(?:[\\w\\u00a1-\\uffff0-9]+-?)*[\\w\\u00a1-\\uffff0-9]+)(?:\\.(?:[\\w\\u00a1-\\uffff0-9]+-)*[\\w\\u00a1-\\uffff0-9]+)*(?:\\.(?:[a-zA-Z\\u00a1-\\uffff]{2,})))(?::\\d{2,5})?(?:\\/[^\\s]*)?$";

    ///
    /// Required methods
    ///
    private IsUrl(PptSlice ppt){ super(ppt); }

    private @Prototype
    IsUrl() { super(); }

    private static @Prototype IsUrl proto = new @Prototype IsUrl();

    // Returns the prototype invariant
    public static @Prototype IsUrl get_proto() { return proto; }

    @Override
    public boolean enabled() {
        return dkconfig_enabled;
    }

    @Override
    public IsUrl instantiate_dyn(@Prototype IsUrl this, PptSlice slice) {
        return new IsUrl(slice);
    }

    // A printed representation for user output
    @SideEffectFree
    @Override
    public String format_using(@GuardSatisfied IsUrl this, OutputFormat format) {
        if (format == OutputFormat.DAIKON) {
            return var().name() + " is Url";
        }

        if (format == OutputFormat.POSTMAN) {
            return "pm.expect(" + getPostmanVariableName(var().name()) + ").to.match(/" + regexPostman + "/)";
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
        assert other instanceof IsUrl;
        return true;
    }

}
