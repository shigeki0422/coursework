// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.jpassion.myproject.mydomain;

import com.jpassion.myproject.mydomain.Flight;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Version;

privileged aspect Flight_Roo_Jpa_Entity {
    
    declare @type: Flight: @Entity;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long Flight.id;
    
    @Version
    @Column(name = "version")
    private Integer Flight.version;
    
    public Long Flight.getId() {
        return this.id;
    }
    
    public void Flight.setId(Long id) {
        this.id = id;
    }
    
    public Integer Flight.getVersion() {
        return this.version;
    }
    
    public void Flight.setVersion(Integer version) {
        this.version = version;
    }
    
}
