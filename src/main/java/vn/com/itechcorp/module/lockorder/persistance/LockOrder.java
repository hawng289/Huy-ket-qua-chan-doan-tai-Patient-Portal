package vn.com.itechcorp.module.lockorder.persistance;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.com.itechcorp.base.persistence.model.AuditableSerialIDEntry;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "lock_order")
@Entity
@Getter @Setter @NoArgsConstructor
public class LockOrder extends AuditableSerialIDEntry {

    @Column(name = "order_number")
    private String orderNumber;

    @Column(name = "pid")
    private String pid;

    @Column(name = "accession_number")
    private String accessionNumber;

    @Column(name = "request")
    private String request;

    @Column(name = "response")
    private String response;

}
