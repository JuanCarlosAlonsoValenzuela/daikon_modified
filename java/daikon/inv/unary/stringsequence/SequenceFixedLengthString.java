package daikon.inv.unary.stringsequence;

import daikon.PptSlice;
import daikon.inv.DiscardInfo;
import daikon.inv.Invariant;
import daikon.inv.InvariantStatus;
import daikon.inv.OutputFormat;
import org.checkerframework.checker.interning.qual.Interned;
import org.checkerframework.checker.lock.qual.GuardSatisfied;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.dataflow.qual.Pure;
import org.checkerframework.dataflow.qual.SideEffectFree;
import org.checkerframework.framework.qual.Unused;
import org.plumelib.util.ArraysPlume;
import org.plumelib.util.Intern;
import typequals.prototype.qual.Prototype;

import static daikon.agora.PostmanUtils.getPostmanVariableName;

/**
 * Represents string sequences that contain a common subset. Prints as {@code {s1, s2, s3, ...}
 * subset of x[]}.
 */
public class SequenceFixedLengthString extends SingleStringSequence {
  // We are Serializable, so we specify a version to allow changes to
  // method signatures without breaking serialization.  If you add or
  // remove fields, you should change this number to the current date.
  static final long serialVersionUID = 20220423L;

  // Variables starting with dkconfig_ should only be set via the
  // daikon.config.Configuration interface.
  public static boolean dkconfig_enabled = false;

  private Integer length = null;

  protected SequenceFixedLengthString(PptSlice ppt) {
    super(ppt);

  }

  protected @Prototype
  SequenceFixedLengthString() {
    super();
  }

  private static @Prototype SequenceFixedLengthString proto = new @Prototype SequenceFixedLengthString();

  /** Returns the prototype invariant for CommonStringSequence. */
  public static @Prototype SequenceFixedLengthString get_proto() {
    return proto;
  }

  /** returns whether or not this invariant is enabled */
  @Override
  public boolean enabled() {
    return dkconfig_enabled;
  }

  /** instantiate an invariant on the specified slice */
  @Override
  protected SequenceFixedLengthString instantiate_dyn(
          @Prototype SequenceFixedLengthString this, PptSlice slice) {
    return new SequenceFixedLengthString(slice);
  }

  // Don't write clone, because this.intersect is read-only
  // protected Object clone();

  @Override
  public String repr(@GuardSatisfied SequenceFixedLengthString this) {
    return "SequenceFixedLengthString " + varNames() + ": length=\"" + length;
  }

  @SideEffectFree
  @Override
  public String format_using(@GuardSatisfied SequenceFixedLengthString this, OutputFormat format) {
    if (format == OutputFormat.DAIKON) {
      return "All the elements of " + var().name() + " have LENGTH=" + length;
    }

    if (format == OutputFormat.POSTMAN) {
      return "pm.expect(" + getPostmanVariableName(var().name()) + ".every(element => element.length === " + length + ")).to.be.true";
    }

    return format_unimplemented(format);

  }


  @Override
  public InvariantStatus check_modified(@Interned String @Interned [] a, int count) {

    if (a==null || a.length == 0) {
      return InvariantStatus.NO_CHANGE;
    }

    // Initialize the length for the first time
    if (length == null) {
      length = a[0].length();
      // Check that all the elements of the array have the same length
      for(int i=0; i<a.length;i++){
        if(a[i].length() != length) {
          return InvariantStatus.FALSIFIED;
        }
      }
    } else {
      for (int i = 0; i<a.length;i++) {
        if(a[i].length() != length) {
          return  InvariantStatus.FALSIFIED;
        }
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
    if(length==null) {
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
    assert other instanceof SequenceFixedLengthString;
    return true;
  }
}
