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

/**
 * Represents string sequences that contain a common subset. Prints as {@code {s1, s2, s3, ...}
 * subset of x[]}.
 */
public class SequenceStringElementsAreHour extends SingleStringSequence {
  // We are Serializable, so we specify a version to allow changes to
  // method signatures without breaking serialization.  If you add or
  // remove fields, you should change this number to the current date.
  static final long serialVersionUID = 20220423L;

  // Variables starting with dkconfig_ should only be set via the
  // daikon.config.Configuration interface.
  public static boolean dkconfig_enabled = Invariant.invariantEnabledDefault;

  // Set to true if the array is empty. If we do not use this property, the invariant would be considered true if all the arrays are empty
  private boolean alwaysEmpty = true;

  protected SequenceStringElementsAreHour(PptSlice ppt) {
    super(ppt);

  }

  protected @Prototype
  SequenceStringElementsAreHour() {
    super();
  }

  private static @Prototype SequenceStringElementsAreHour proto = new @Prototype SequenceStringElementsAreHour();

  /** Returns the prototype invariant for CommonStringSequence. */
  public static @Prototype SequenceStringElementsAreHour get_proto() {
    return proto;
  }

  /** returns whether or not this invariant is enabled */
  @Override
  public boolean enabled() {
    return dkconfig_enabled;
  }

  /** instantiate an invariant on the specified slice */
  @Override
  protected SequenceStringElementsAreHour instantiate_dyn(
          @Prototype SequenceStringElementsAreHour this, PptSlice slice) {
    return new SequenceStringElementsAreHour(slice);
  }

  // Don't write clone, because this.intersect is read-only
  // protected Object clone();

  @Override
  public String repr(@GuardSatisfied SequenceStringElementsAreHour this) {
    return "SequenceStringElementsAreHour " + varNames();
  }

  @SideEffectFree
  @Override
  public String format_using(@GuardSatisfied SequenceStringElementsAreHour this, OutputFormat format) {
    return "All the elements of " + var().name() + " are Hours: HH:MM 24-hour format, optional leading 0";
  }


  @Override
  public InvariantStatus check_modified(@Interned String @Interned [] a, int count) {

    Pattern pattern = Pattern.compile("^([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$");

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
    assert other instanceof SequenceStringElementsAreHour;
    return true;
  }
}
