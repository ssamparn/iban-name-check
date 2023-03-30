package com.sparkle.demo.ibannamecheckblockingimpl.database.relationship;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import java.util.List;
import java.util.UUID;

@Entity(name = "file_request")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "file_request")
public class FileRequestEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator",
            parameters = {
                    @Parameter(
                            name = "uuid_gen_strategy_class",
                            value = "org.hibernate.id.uuid.CustomVersionOneStrategy"
                    )
            }
    )
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "request_id", nullable = false)
    private String requestId;

    @Column(name = "task_id")
    private String taskId;

    @OneToMany(fetch = FetchType.EAGER, targetEntity = FileRequestContentEntity.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "file_request_id")
    private List<FileRequestContentEntity> fileRequestContents;
}
