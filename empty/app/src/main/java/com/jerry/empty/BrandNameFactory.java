package com.jerry.empty;

public class BrandNameFactory {
    private static String[] allBrands = null;

    public static String[] getBrandNames() {
        if (allBrands == null) {
            allBrands = new String[]{"cavatappi", "cave-b", "cave-de-genouilly", "cave-de-la-cote", "cave-de-rasteau", "cave-de-tain"};
        }
        return allBrands;

    }
}
