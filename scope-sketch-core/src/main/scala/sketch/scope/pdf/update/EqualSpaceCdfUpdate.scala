package sketch.scope.pdf.update

import sketch.scope.cmap.Cmap
import sketch.scope.hcounter.HCounter
import sketch.scope.pdf.{Count, Prim, Sketch}
import sketch.scope.plot._

/**
  * Licensed by Probe Technology, Inc.
  */
trait EqualSpaceCdfUpdate {

  def updateCmap(sketch: Sketch[_], ps: List[(Prim, Count)], mixingRatio: Double, window: Double): Option[Cmap] = for {
    sketchPlot <- sketch.densityPlot
    mtpSketchPlot = sketchPlot * (1 / (mixingRatio + 1))
    mtpPsPlot = DensityPlot.squareKernel(ps, window) * (mixingRatio / (mixingRatio + 1))
    mergedPlot = mtpSketchPlot + mtpPsPlot
    cmapSize = sketch.conf.cmap.size
    cmap = cmapForEqualSpaceCumulative(mergedPlot, cmapSize)
  } yield cmap

  def cmapForEqualSpaceCumulative(plot: DensityPlot, cmapSize: Int): Cmap = {
    val cdf = plot.cumulative
    val invCdf = cdf.inverse
    val unit = cdf.interpolation(Double.MaxValue) / cmapSize.toDouble

    val divider = (1 until cmapSize).toList.map(i => i * unit).map(a => invCdf.interpolation(a))
    Cmap.divider(divider)
  }

}

object EqualSpaceCdfUpdate extends EqualSpaceCdfUpdate