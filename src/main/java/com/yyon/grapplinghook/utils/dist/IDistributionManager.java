/*
 * https://github.com/OrionDevelopment/Multi-Platform-Template
 */

package com.yyon.grapplinghook.utils.dist;

/**
 * Manages the properties of the current distribution and gives access to the current distribution.
 */
public interface IDistributionManager
{
    /**
     * Gives access to the distribution manager.
     *
     * @return The distribution manager.
     */
    static IDistributionManager getInstance() {
        return FabricDistributionManager.getInstance();
    }

    /**
     * The current distribution.
     *
     * @return The current distribution.
     */
    Dist getCurrentDistribution();

    /**
     * Indicates if the current platform is running in production mode or not.
     *
     * @return True if the current platform is running in production mode. False when not.
     */
    boolean isProduction();
}