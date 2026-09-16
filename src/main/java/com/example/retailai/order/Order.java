package com.example.retailai.order;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_number", unique = true, nullable = false)
    private String orderNumber;

    @Column(nullable = false)
    private String customerName;

    @Column(nullable = false)
    private String status;

    private String carrier;
    private String trackingNumber;
    private LocalDate estimatedDelivery;

    @Column(nullable = false)
    private BigDecimal totalAmount;

    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public String getOrderNumber() { return orderNumber; }
    public String getCustomerName() { return customerName; }
    public String getStatus() { return status; }
    public String getCarrier() { return carrier; }
    public String getTrackingNumber() { return trackingNumber; }
    public LocalDate getEstimatedDelivery() { return estimatedDelivery; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public void setStatus(String status) { this.status = status; }
    public void setCarrier(String carrier) { this.carrier = carrier; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }
    public void setEstimatedDelivery(LocalDate estimatedDelivery) { this.estimatedDelivery = estimatedDelivery; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
