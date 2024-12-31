package daikon.inv.unary.stringsequence.dates;

import daikon.PptSlice;
import daikon.inv.DiscardInfo;
import daikon.inv.Invariant;
import daikon.inv.InvariantStatus;
import daikon.inv.OutputFormat;
import daikon.inv.unary.stringsequence.SingleStringSequence;
import org.checkerframework.checker.interning.qual.Interned;
import org.checkerframework.checker.lock.qual.GuardSatisfied;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.dataflow.qual.Pure;
import org.checkerframework.dataflow.qual.SideEffectFree;
import typequals.prototype.qual.Prototype;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static daikon.agora.PostmanUtils.getPostmanVariableName;

/**
 * Represents string sequences that contain a common subset. Prints as {@code {s1, s2, s3, ...}
 * subset of x[]}.
 */
public class SequenceStringElementsAreDateDDMMYYYY extends SingleStringSequence {
  // We are Serializable, so we specify a version to allow changes to
  // method signatures without breaking serialization.  If you add or
  // remove fields, you should change this number to the current date.
  static final long serialVersionUID = 20220423L;

  // Variables starting with dkconfig_ should only be set via the
  // daikon.config.Configuration interface.
  public static boolean dkconfig_enabled = false;

  // Set to true if the array is empty. If we do not use this property, the invariant would be considered true if all the arrays are empty
  private boolean alwaysEmpty = true;

  private static String regex = "^(?:0[1-9]|[12][0-9]|3[01])[-/.](?:0[1-9]|1[012])[-/.](?:1\\d{3}|20[01234][0-9]|2050|9999)$";

  protected SequenceStringElementsAreDateDDMMYYYY(PptSlice ppt) {
    super(ppt);

  }

  protected @Prototype
  SequenceStringElementsAreDateDDMMYYYY() {
    super();
  }

  private static @Prototype SequenceStringElementsAreDateDDMMYYYY proto = new @Prototype SequenceStringElementsAreDateDDMMYYYY();

  /** Returns the prototype invariant for CommonStringSequence. */
  public static @Prototype SequenceStringElementsAreDateDDMMYYYY get_proto() {
    return proto;
  }

  /** returns whether or not this invariant is enabled */
  @Override
  public boolean enabled() {
    return dkconfig_enabled;
  }

  /** instantiate an invariant on the specified slice */
  @Override
  protected SequenceStringElementsAreDateDDMMYYYY instantiate_dyn(
          @Prototype SequenceStringElementsAreDateDDMMYYYY this, PptSlice slice) {
    return new SequenceStringElementsAreDateDDMMYYYY(slice);
  }

  // Don't write clone, because this.intersect is read-only
  // protected Object clone();

  @Override
  public String repr(@GuardSatisfied SequenceStringElementsAreDateDDMMYYYY this) {
    return "SequenceStringElementsAreDateDDMMYYYY " + varNames();
  }

  @SideEffectFree
  @Override
  public String format_using(@GuardSatisfied SequenceStringElementsAreDateDDMMYYYY this, OutputFormat format) {
    if (format == OutputFormat.DAIKON) {
      return "All the elements of " + var().name() + " are dates. Format: DD/MM/YYYY";
    }

    if (format == OutputFormat.POSTMAN) {
      return "pm.expect(" + getPostmanVariableName(var().name()) + ".every(element => /" + regex + "/.test(element))).to.be.true";
    }

    return format_unimplemented(format);

  }


  @Override
  public InvariantStatus check_modified(@Interned String @Interned [] a, int count) {

    /*
     *   The regex matches on a date with the DD/MM/YYYY format (Year min: 1900, Year max: 2050).
     *   For example:
     *       - 01/12/1900
     *       - 25.01.2019
     *       - 30-10-2050
     */
    // ^(?:0[1-9]|[12][0-9]|3[01])[-/.](?:0[1-9]|1[012])[-/.](?:19\d{2}|20[01234][0-9]|2050)$
    Pattern pattern = Pattern.compile(regex);

    if(a.length>0){
      alwaysEmpty = false;
    }

    for(int i=0; i<a.length; i++) {
      Matcher matcher = pattern.matcher(a[i]);
      if(!matcher.matches()) {
        return InvariantStatus.FALSIFIED;
      }
    }

    return InvariantStatus.NO_CHANGE;

  }

  @Override
  public InvariantStatus add_modified(@Interned String @Interned [] a, int count) {
    return check_modified(a, count);
  }

  @Override
  protected double computeConfidence() {

    if(alwaysEmpty) {
      return Invariant.CONFIDENCE_UNJUSTIFIED;
    }

    return 1 - Math.pow(.1, ppt.num_samples());
  }

  @Pure
  public @Nullable DiscardInfo isObviousImplied() {
    return null;
  }

  @Pure
  @Override
  public boolean isSameFormula(Invariant other) {
    assert other instanceof SequenceStringElementsAreDateDDMMYYYY;
    return true;
  }
}
