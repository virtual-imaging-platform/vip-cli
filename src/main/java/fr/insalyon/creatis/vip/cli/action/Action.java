/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.insalyon.creatis.vip.cli.action;

import fr.insalyon.creatis.vip.java_client.ApiException;

/**
 *
 * @author qzhang
 */
interface  Action<T> {
    

    
    T execute() throws ApiException;
    

}
