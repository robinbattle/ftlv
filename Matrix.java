import java.io.Serializable;

/*
 * Copyright (C) 2005-2011 Brian K. Vogel
 * 
 * This file is part of PNET.
 *
 *  PNET is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  PNET is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with PNET.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

 
/**
 * An instance of this class represents a dense matrix of floats in column major
 * order. This matrix is backed by a 1-D array of floats. Column major storage is
 * used for compatability with native BLAS libraries.
 *
 * @author Brian K. Vogel (brian@brianvogel.com)
 */
public class Matrix implements Serializable{

    /**
     * Create a new <i>rows</i> by <i>cols</i> matrix, initialized
     * to all zeros.
     *
     * @param rows
     * @param cols
     */
    public Matrix(int rows, int cols) {

        height = rows;
        width = cols;
        values = new float[rows*cols];

    }

    // Public members

    // The number of rows in this matrix.
    public int height;

    // The number of columns in this matrix.
    public int width;

    // The backing array for this matrix. The components of the matrix are stored in this
    // array in column-major ordering.
    public float[] values;

    /**
     * Place <i>val</i> into the element at row = <i>row</i> and column = <i>column</i>.
     *
     * @param row The row index to set.
     * @param column The column index to set.
     * @param val The value to set.
     */
    public void set(int row, int column, float val) {
        values[row + height*column] = val;
    }


    /**
     * Get the element at the specified row and column.
     *
     * @param row
     * @param column
     * @return
     */
    public float get(int row, int column) {
        return values[row + height*column];
    }
    
    public float get(int index){
    	return values[index];
    }
    
    @Override
    public String toString() {
        String out = "";
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                out += get(i,j) + "   ";
            }
            out += "\n";
        }
        return out;
    }
    
    public int getHeight(){
    	return height;
    }
    
    public int getWidth(){
    	return width;
    }


}
