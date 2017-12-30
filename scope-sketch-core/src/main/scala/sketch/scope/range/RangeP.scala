package sketch.scope.range

import sketch.scope.measure._
import sketch.scope.measure.syntax._
import sketch.scope.pdf.Prim
import sketch.scope.range.syntax._

import scala.collection.immutable.NumericRange

/**
  * Licensed by Probe Technology, Inc.
  *
  * Range with primitive type of Sketch.
  */
trait GenericRangeP[G] extends RangeM[Prim] {
  def measure: Measure[Prim] = doubleMeasure
  def start: Prim
  def end: Prim
}

trait RangePOps extends RangeMOps[GenericRangeP] {

  def intersection[A](range1: RangeP, range2: RangeP): RangeP = {
    val (start1, end1) = (range1.start, range1.end)
    val (start2, end2) = (range2.start, range2.end)

    val start = if(start1 > start2) start1 else start2
    val end = if(end1 > end2) end2 else end1

    RangeP(start, end)
  }

  def overlapPercent[A](range1: RangeP, range2: RangeP): Double = {
    val range1Len = length(range1)

    if (range1Len != 0) (length(intersection(range1, range2)) / length(range1)).toDouble
    else if(range1 == range2) 1
    else 0
  }

}

trait RangePSyntax {

  type RangeP = GenericRangeP[Nothing]

  implicit class RangeImpl(range: RangeP) {
    def length: BigDecimal = RangeP.length(range)
    def overlapPercent(range2: RangeP): Double = RangeP.overlapPercent(range, range2)
  }

}

object RangeP extends RangePOps {

  private case class RangePImpl(start: Prim, end: Prim) extends RangeP

  def apply(start: Prim, end: Prim): RangeP = bare(start, end)

  def bare(start: Prim, end: Prim): RangeP = {
    if(start < end) RangePImpl(start, end) else RangePImpl(end, start)
  }

  def point(p: Prim): RangeP = bare(p, p)

  def forNumericRange(numRange: NumericRange[Prim]): RangeP = apply(numRange.start, numRange.end)

  def forRangeM[A](rangeM: RangeM[A]): RangeP = {
    bare(rangeM.measure.to(rangeM.start), rangeM.measure.to(rangeM.end))
  }

}
