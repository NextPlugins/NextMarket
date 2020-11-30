package com.nextplugins.nextmarket.api;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author Henry Fábio
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NextMarketAPI {

    @Getter public static final NextMarketAPI instance = new NextMarketAPI();



}
