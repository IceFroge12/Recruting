package ua.kpi.nc.service;

import ua.kpi.nc.persistence.model.SocialNetwork;

/**
 * Created by IO on 16.04.2016.
 */
public interface SocialNetworkService {

    SocialNetwork getByID(Long id);

}
