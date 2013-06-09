/**
 * Copyright (c) 2011, The University of Southampton and the individual contributors.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *   * 	Redistributions of source code must retain the above copyright notice,
 * 	this list of conditions and the following disclaimer.
 *
 *   *	Redistributions in binary form must reproduce the above copyright notice,
 * 	this list of conditions and the following disclaimer in the documentation
 * 	and/or other materials provided with the distribution.
 *
 *   *	Neither the name of the University of Southampton nor the names of its
 * 	contributors may be used to endorse or promote products derived from this
 * 	software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.openimaj.image.feature.dense.gradient;

import java.util.ArrayList;
import java.util.List;

import org.openimaj.image.FImage;
import org.openimaj.image.analyser.ImageAnalyser;
import org.openimaj.image.analysis.algorithm.BinnedImageHistogramAnalyser;
import org.openimaj.image.pixel.sampling.QuadtreeSampler;
import org.openimaj.image.processing.convolution.FImageGradients;
import org.openimaj.image.processing.edges.CannyEdgeDetector;
import org.openimaj.math.geometry.shape.Rectangle;
import org.openimaj.math.statistics.distribution.Histogram;

public class PyramidHistogramOfGradients implements ImageAnalyser<FImage> {
	BinnedImageHistogramAnalyser histExtractor;
	FImage magnitudes;
	int nlevels;

	@Override
	public void analyseImage(FImage image) {
		final FImage edges = image.process(new CannyEdgeDetector());
		final FImageGradients gmo = FImageGradients.getGradientMagnitudesAndOrientations(image);

		this.magnitudes = gmo.magnitudes.multiplyInplace(edges);
		this.histExtractor.analyseImage(gmo.orientations);
	}

	public Histogram extractFeature(Rectangle rect) {
		final QuadtreeSampler sampler = new QuadtreeSampler(rect, nlevels);
		final List<float[]> parts = new ArrayList<float[]>();
		final Histogram hist = new Histogram(0);

		for (final Rectangle r : sampler) {
			hist.combine(histExtractor.computeHistogram(r, magnitudes));
		}

		return hist;
	}
}
