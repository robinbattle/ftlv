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
 * This class contains various static methods for computing with matrices, such
 * as matrix products, matrix sums, etc.
 *
 * @author Brian K. Vogel (brian@brianvogel.com)
 */
public class MatrixUtilities {


    /**
     * Compute B x C and place the result in A. Note: all three matrices must
     * have already been allocated.
     *
     * Note: This method implements the basic easy-to-understand version. It is
     * not optimized in any way.
     *
     * @param A
     * @param B
     * @param C
     */
    public static void matMultiplySlow(Matrix A, Matrix B, Matrix C) {
        int rowsOut = B.height;
        int innerDim = B.width;
        int colsOut = C.width;
        if ((A.height != rowsOut) || (A.width != C.width) || (B.width != C.height)) {
            throw new RuntimeException("Error: The matrices have inconsistant dimensions!");
        }
        float sum;        
        // For each row of B
        for (int i=0; i<rowsOut; i++) {
            // For each column of C
            for (int j=0; j<colsOut; j++) {
                // Compute dot product of row i of B with column j of C.
                sum = 0;
                for (int k=0; k<innerDim; k++) {
                    sum += B.get(i, k)*C.get(k, j);
                }
                A.set(i, j, sum);
            }
        }
    }

    

    /**
     * Compute B x C and place the result in A. Note: all three matricies must
     * have already been allocated.
     *
     *
     * @param A
     * @param B
     * @param C
     */
    public static void matMultiply(Matrix A, Matrix B, Matrix C) {
        int rowsOut = B.height;
        int innerProdDim = B.width;
        int colsOut = C.width;
        if ((A.height != rowsOut) || (A.width != C.width) || (B.width != C.height)) {
            throw new RuntimeException("Error: The matrices have inconsistant dimensions!");
        }
        float sum;
        // matrices are in column-major, so use a temporary array containign
        // the current row of B.
        float[] rowB = new float[innerProdDim];
        // Now, for each row of B, compute inner products with all columns of C.
        for (int i=0; i < rowsOut; i++) {
            // i = current row of the output matrix.
            // Copy the i'th row of B into the temp array.
            for (int n=0; n < innerProdDim; n++) {
                rowB[n] = B.get(i, n);
            }
            // Now perform the inner products for each column of C.
            for (int j=0; j < colsOut; j++) {
                sum = 0;
                for (int m = 0; m < innerProdDim; m++) {
                    sum += rowB[m]*C.get(m, j);
                }
                A.set(i,j, sum);
            }
        }
    }

   
      

    /**
     * Compute the element-wise product of B and C and then place the result in A.
     * Note: these matrices must have already been allocated and must have the
     * same dimensions. Otherwise, a runtime exception will occur.
     *
     * @param A 
     * @param B
     * @param C
     */
    public static void elementWiseMultiply(Matrix A, Matrix B, Matrix C) {
        _checkDimensions(A, B, C);
        int rowsA = A.height;
        int colsA = A.width;
        float[] backingArrayA = A.values;
        float[] backingArrayB = B.values;
        float[] backingArrayC = C.values;
        for (int i=0; i < rowsA*colsA; i++) {
            backingArrayA[i] = backingArrayB[i] * backingArrayC[i];
        }
    }

    /**
     * Compute the element-wise division B / C and then place the result in A.
     * Note: these matricies must have already been allocated and must have the
     * same dimensions. Otherwise, a runtime exception will occur.
     *
     * Specifically, compute:
     * 
     *      B + epsilon
     * A = ------------
     *      C + epsilon
     *
     * @param A
     * @param B
     * @param C
     * @param epsilon A positive constant that is added to both the numerator and denominator.
     */
    public static void elementWiseDivide(Matrix A, Matrix B, Matrix C, float epsilon) {
        _checkDimensions(A, B, C);
        int rowsA = A.height;
        int colsA = A.width;      
        float[] backingArrayA = A.values;
        float[] backingArrayB = B.values;
        float[] backingArrayC = C.values;
        for (int i=0; i < rowsA*colsA; i++) {
            backingArrayA[i] = (backingArrayB[i] + epsilon) / (backingArrayC[i] + epsilon);
        }
    }

    /**
     * Compute the element-wise difference (B-C) and then place the result in A.
     * Note: these matricies must have already been allocated and must have the
     * same dimensions. Otherwise, a runtime exception will occur.
     * @param A The result is returned in this matrix.
     * @param B
     * @param C
     */
    public static void elementWiseDifference(Matrix A, Matrix B, Matrix C) {
        _checkDimensions(A, B, C);
        int rowsA = A.height;
        int colsA = A.width;       
        float[] backingArrayA = A.values;
        float[] backingArrayB = B.values;
        float[] backingArrayC = C.values;
        for (int i=0; i < rowsA*colsA; i++) {
            backingArrayA[i] = backingArrayB[i] - backingArrayC[i];
        }        
    }

    /**
     * Compute the element-wise square A.^2 and then place the result in A.
     * Note: A must have already been initialized.
     * @param A Compute the element-wise square and put the result back in A.
     */
    public static void elementWiseSquare(Matrix A) {
        int rowsA = A.height;
        int colsA = A.width;
        float[] backingArrayA = A.values;
        for (int i=0; i < rowsA*colsA; i++) {
            backingArrayA[i] = backingArrayA[i] * backingArrayA[i];
        }
    }

    /**
     * Compute the sum of all elements in <i>A</i> and return it.
     * @param A The input matrix.
     * @return The sum of all eleements in A
     */
    public static float sum(Matrix A) {
        int rowsA = A.height;
        int colsA = A.width;
        float[] backingArrayA = A.values;
        float sum = 0;
        for (int i=0; i < rowsA*colsA; i++) {
            sum += backingArrayA[i];
        }
        return sum;
    }

    /**
     * Compute the transpose of B and put the result in A.
     * Both A and B must have already been initialized to consitent dimensions.
     *
     * @param A The result matrix.
     * @param B The input matrix.
     */
    public static void transpose(Matrix A, Matrix B) {
        int rowsB = B.height;
        int colsB = B.width;
        int rowsA = A.height;
        int colsA = A.width;
        if ((rowsA != colsB) || (colsA != rowsB)) {
            throw new RuntimeException("Error: The matrices have inconsistant dimensions!");
        }
        // For each row of B
        for (int i=0; i<rowsB; i++) {
            // For each column of C
            for (int j=0; j<colsB; j++) {
                A.set(j, i, B.get(i,j));
            }
        }
    }

    /**
     * Set all elements of the matrix <i>A</i> to have value <i>value</i> and
     * return the result in <i>A</i>.
     * @param A
     * @param value
     */
    public static void setValue(Matrix A, float value) {
        int rowsA = A.height;
        int colsA = A.width;
        float[] backingArrayA = A.values;
        for (int i=0; i < rowsA*colsA; i++) {
            backingArrayA[i] = value;
        }
    }

    /**
     * Set all elements of the matrix <i>A</i> to have random values and return the
     * result in <i>A</i>. The values are chosen to be uniformly distributed between
     * 0.0 and 1.0.
     * @param A
     */
    public static void randomize(Matrix A) {
        int rowsA = A.height;
        int colsA = A.width;
        float[] backingArrayA = A.values;
        for (int i=0; i < rowsA*colsA; i++) {
            backingArrayA[i] = (float)java.lang.Math.random();
        }
    }


    /**
     * Return true only if A and B have the same dimensions. Otherwise throw
     * an exception.
     * @param A
     * @param B
     * @return True if no exception is thrown.
     */
    private static boolean  _checkDimensions(Matrix A, Matrix B) {
        int rowsA = A.height;
        int colsA = A.width;
        int rowsB = B.height;
        int colsB = B.width;
        if ((rowsA == rowsB) && (colsA == colsB)) {
              return true;
        } else {
            throw new RuntimeException("Error: Inconsistent matrix dimensions!");
        }
    }

    /**
     * Return true only if A, B, and C have the same dimensions. Otherwise throw
     * an exception.
     * @param A
     * @param B
     * @param C
     * @return True if no exception is thrown.
     */
    private static boolean  _checkDimensions(Matrix A, Matrix B, Matrix C) {
        if (_checkDimensions(A, B) && _checkDimensions(A, C)) {
            return true;
        } else {
            throw new RuntimeException("Error: Inconsistent matrix dimensions!");
        }

    }

}
