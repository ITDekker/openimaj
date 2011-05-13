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
package org.openimaj.image.feature.local.keypoints;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

import org.openimaj.feature.ByteFV;
import org.openimaj.feature.FeatureVector;
import org.openimaj.feature.local.LocalFeature;
import org.openimaj.io.VariableLength;
import org.openimaj.math.geometry.point.Point2d;
import org.openimaj.math.geometry.point.Point2dImpl;
import org.openimaj.math.geometry.point.ScaleSpacePoint;

import Jama.Matrix;


/**
 * 
 * @author Jonathon Hare
 *
 */
public class Keypoint implements Serializable, ScaleSpacePoint, LocalFeature, VariableLength {
	static final long serialVersionUID = 1234554345;
	
	private final static int DEFAULT_LENGTH = 128;
	
	/**
	 * keypoint feature descriptor (i.e. SIFT)
	 */
	public byte [] ivec;
	
	/**
	 * dominant orientation of keypoint 
	 */
	public float ori;
	
	/**
	 * scale of keypoint
	 */
	public float scale;
	
	/**
	 * x-position of keypoint
	 */
	public float x;
	
	/**
	 * y-position of keypoint
	 */
	public float y;

	public Keypoint() {
		this.ivec = new byte[DEFAULT_LENGTH];
	}
	
	public Keypoint(int len) {
		this.ivec = new byte[len];
	}
	
	public Keypoint(float x, float y, float ori, float scale, byte [] ivec) {
		this.x = x;
		this.y = y;
		this.ori = ori;
		this.scale = scale;
		this.ivec = ivec;
	}
	
	public Keypoint(Keypoint k) {
		this(k.x, k.y, k.ori, k.scale, Arrays.copyOf(k.ivec, k.ivec.length));
	}
		
	@Override
	public Float getOrdinate(int dimension) {
		if (dimension == 0) return x;
		if (dimension == 1) return y;
		if (dimension == 2) return scale;
		return null;
	}

	@Override
	public int getDimensions() { return 3; }
		
	@Override
	public float getX() {
		return x;
	}
	
	@Override
	public float getY() {
		return y;
	}
	
	@Override
	public void setX(float x) {
		this.x = x;
	}
	
	@Override
	public void setY(float y) {
		this.y = y;
	}
	
	@Override
	public float getScale() {
		return scale;
	}

	@Override
	public void setScale(float scale) {
		this.scale = scale;		
	}
	
	@Override
	public String toString() {
		return ("Keypoint("+this.x+", "+this.y+", "+this.scale+","+this.ori+")");
	}
	
	public boolean locationEquals(Object obj) {
		if (obj instanceof Keypoint) {
			Keypoint kobj = (Keypoint)obj;
			
			if (kobj.x == x && kobj.y == y && kobj.scale == scale) return true;
		}
		
		return super.equals(obj);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Keypoint) {
			Keypoint kobj = (Keypoint)obj;
			
			if (kobj.x == x && kobj.y == y && kobj.scale == scale && Arrays.equals(ivec, kobj.ivec)) return true;
		}
		
		return super.equals(obj);
	}
	
	@Override
	public int hashCode() { 
	    int hash = 1;
	    hash = hash * 31 + Float.floatToIntBits(y);
	    hash = hash * 31 + Float.floatToIntBits(x);
	    hash = hash * 31 + Float.floatToIntBits(scale);
	    return hash;
	  }	

	@Override
	public Keypoint clone() {
		Keypoint clone = new Keypoint();
		
		clone.x = x;
		clone.ori = ori;
		clone.y = y;
		clone.scale = scale;
		
		clone.ivec = new byte[ivec.length];
		System.arraycopy(ivec, 0, clone.ivec, 0, ivec.length);
		
		return clone;
	}

	@Override
    public void copyFrom( Point2d p )
    {
		setX( p.getX() );
		setY( p.getY() );
    }
	
	@Override
	public void writeBinary(DataOutput out) throws IOException {
		getLocation().writeBinary(out);
		out.write(this.ivec);
	}
	@Override
	public void writeASCII(PrintWriter out) throws IOException {
		/* Output data for the keypoint. */
		getLocation().writeASCII(out);
		for (int i = 0; i < ivec.length; i++) {
			if (i>0 && i % 20 == 0)
				out.println();
			out.print(" " + (ivec[i]+128));
		}
		out.println();
	}
	
	@Override
	public Keypoint readBinary(DataInput in) throws IOException {
		setLocation(getLocation().readBinary(in));
		in.readFully(ivec);
		
		return this;
	}
	
	@Override
	public Keypoint readASCII(Scanner in) throws IOException {
		setLocation(getLocation().readASCII(in));
		
		int i = 0;
		while (i < ivec.length) {
			String line = in.nextLine();
			StringTokenizer st = new StringTokenizer(line);
			
			while (st.hasMoreTokens()) {
				ivec[i] = (byte) (Integer.parseInt( st.nextToken() ) - 128);
				i++;
			}
		}
		
		return this;
	}
	
	@Override
	public byte[] binaryHeader() {
		return "".getBytes();
	}
	
	@Override
	public String asciiHeader() {
		return "";
	}
	
	@Override
	public FeatureVector getFeatureVector() {
		return new ByteFV(ivec);
	}
	
	@Override
	public KeypointLocation getLocation() {
		return new KeypointLocation(x, y, ori, scale);
	}
	
	public void setLocation(KeypointLocation location){
		x = location.x;
		y = location.y;
		scale = location.scale;
		ori = location.orientation;
	}
	
	public static List<Keypoint> getRelativeKeypoints(List<Keypoint> keypoints, float x, float y) {
		List<Keypoint> shifted = new ArrayList<Keypoint>();
		for(Keypoint old:keypoints){
			Keypoint n = new Keypoint();
			n.x = old.x - x;
			n.y = old.y - y;
			n.ivec = old.ivec;
			n.scale = old.scale;
			n.ori = old.ori;
			shifted.add(n);
		}
		return shifted ;
	}
	
	public static List<Keypoint> getScaledKeypoints(List<Keypoint> keypoints, int toScale) {
		List<Keypoint> shifted = new ArrayList<Keypoint>();
		for(Keypoint old:keypoints){
			Keypoint n = new Keypoint();
			n.x = old.x*toScale;
			n.y = old.y*toScale;
			n.ivec = old.ivec;
			n.scale = old.scale*toScale;
			n.ori = old.ori;
			shifted.add(n);
		}
		return shifted ;
	}

	@Override
	public void translate(float x, float y) {
		this.x += x;
		this.y += y;
	}

	@Override
	public Keypoint transform(Matrix transform) {
		float xt = (float)transform.get(0, 0) * getX() + (float)transform.get(0, 1) * getY() + (float)transform.get(0, 2);
		float yt = (float)transform.get(1, 0) * getX() + (float)transform.get(1, 1) * getY() + (float)transform.get(1, 2);
		float zt = (float)transform.get(2, 0) * getX() + (float)transform.get(2, 1) * getY() + (float)transform.get(2, 2);
		
		xt /= zt;
		yt /= zt;
		
		return new Keypoint(xt,yt,this.ori,this.scale,this.ivec.clone());
	}
}
