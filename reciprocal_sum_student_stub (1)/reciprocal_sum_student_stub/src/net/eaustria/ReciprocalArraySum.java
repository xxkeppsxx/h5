/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.eaustria;

/**
 *
 * @author bmayr
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

/**
 * Class wrapping methods for implementing reciprocal array sum in parallel.
 */
public final class ReciprocalArraySum {

    /**
     * Default constructor.
     */
    private ReciprocalArraySum() {
    }

    /**
     * Sequentially compute the sum of the reciprocal values for a given array.
     *
     * @param input Input array
     * @return The sum of the reciprocals of the array input
     */
    protected static double seqArraySum(final double[] input) {
        double sum = 0;
        for(int i = 0;i<input.length;i++){
            sum += (1/input[i]);
        }
        return sum;

        // ToDo: Compute sum of reciprocals of array elements
       
    }
  

    /**
     * This class stub can be filled in to implement the body of each task
     * created to perform reciprocal array sum in parallel.
     */
    private static class ReciprocalArraySumTask extends RecursiveAction {
        /**
         * Starting index for traversal done by this task.
         */
        private final int startIndexInclusive;
        /**
         * Ending index for traversal done by this task.
         */
        private final int endIndexExclusive;
        /**
         * Input array to reciprocal sum.
         */
        private final double[] input;
        /**
         * Intermediate value produced by this task.
         */
        private double value;
        
        private static int SEQUENTIAL_THRESHOLD = 50000;

        /**
         * Constructor.
         * @param setStartIndexInclusive Set the starting index to begin
         *        parallel traversal at.
         * @param setEndIndexExclusive Set ending index for parallel traversal.
         * @param setInput Input values
         */
        ReciprocalArraySumTask(final int setStartIndexInclusive, final int setEndIndexExclusive, final double[] setInput) {
            this.startIndexInclusive = setStartIndexInclusive;
            this.endIndexExclusive = setEndIndexExclusive;
            this.input = setInput;
        }

        /**
         * Getter for the value produced by this task.
         * @return Value produced by this task
         */
        public double getValue() {
            return value;
        }

        @Override
        protected void compute() {
            int endstart = endIndexExclusive - startIndexInclusive;
            if(endstart <= SEQUENTIAL_THRESHOLD){
                double [] arr = new double[endstart];
                for (int i = 0; i<endstart;i++){
                    arr[i] = input[i + startIndexInclusive];
                }
                double sum = seqArraySum(arr);
                this.value = sum;
            }else{

                int mid = input.length/2;
                ReciprocalArraySumTask leftTask = new ReciprocalArraySumTask(startIndexInclusive,mid,input);
                ReciprocalArraySumTask rightTask = new ReciprocalArraySumTask(mid,endIndexExclusive,input);
                invokeAll(leftTask,rightTask);
                this.value = leftTask.getValue() + rightTask.getValue();
            }

        }
    }


    protected static double parManyTaskArraySum(final double[] input, final int numTasks) {
        ForkJoinPool pool = new ForkJoinPool(numTasks);
        ReciprocalArraySumTask task = new ReciprocalArraySumTask(0,input.length,input);
        pool.invoke(task);
        return task.getValue();
    }

    public static void main(String[] args) {

        //double[] input = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20};
        double[] input = new double[10];
        for (int i = 0; i < 10; i++) {
            input[i] = i+1;
        }
        System.out.println("Sum: " + parManyTaskArraySum(input, 2));
    }

}

