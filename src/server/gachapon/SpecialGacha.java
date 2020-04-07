/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.gachapon;

/**
 *
 * @author Nanson
 */
public class SpecialGacha extends GachaponItems {

    @Override
    public int[] getCommonItems() {
        return new int[]{
            //SCG, Flamekeeper 
            1082223, 1082246,

            //Pink Gaia, Pink Adv, Purple Adv, Purple Gaia, Blackfist, Zakum
            1102084, 1102041, 1102042, 1102086, 1102206, 1102194,

            //Spiegelmann's x 2, Lilin's (2nd)
            1112401, 1112402, 1112414,

            //Royal Red Mardi Gras, Spectrum, Tree Branch x4
            1012186, 1022082, 1012058, 1012059, 1012060, 1012061,

            // Crystal Leaf Earrings
            1032048,

            //Immortal Pharaoh Belt
            1132013,

            //FaceStompers, Yellow Snowshoes
            1072344, 1072239
        };
    }

    @Override
    public int[] getUncommonItems() {
        return new int[]{};
    }

    @Override
    public int[] getRareItems() {
        return new int[]{};
    }
    
}
