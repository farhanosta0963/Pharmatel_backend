package com.pharmatel.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "medicine")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Medicine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @Column(name = "buy_price")
    private String buyPrice;

    @Column(name = "sell_price")
    private String sellPrice;

    @Column(name = "pharmaceutical_form")
    private String pharmaceuticalForm;

    @Column(name = "box")
    private String box;

    @Column(name = "drug_composition")
    private String drugComposition;

    @Column(name = "capacity")
    private String capacity;

    
    private String factory;


    @Column(name = "by_pharmacist")
    private Boolean byPharmacist;

    @OneToOne // TODO we need to fix this relationship in navicat diagram
    @JoinColumn(name = "account_id")
    private Account account;



}
