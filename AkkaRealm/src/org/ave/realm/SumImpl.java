
package org.ave.realm;

import org.ave.core.Flow;


/**
 * User code logic implementation
 * 
 */
public class SumImpl
    extends org.ave.realm.Sum.UserLogic
{


    /**
     * User code block implementation for - MakeSum.
     * 
     * @param B
     *     Unboxed message data
     * @param MakeSum
     *     Unboxed message data
     * @param Result
     *     Block results flow
     */
    @Override
    public void MakeSum(Float MakeSum, Float B, Flow<Float> Result) {
        Float res = MakeSum + B;
        Result.send(res);
        System.out.println("Finished: " + res);
    }

}
