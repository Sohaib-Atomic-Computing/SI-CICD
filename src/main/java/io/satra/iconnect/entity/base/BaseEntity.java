package io.satra.iconnect.entity.base;


import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.GenericGenerator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseEntity {
	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(
      name = "UUID",
      strategy = "org.hibernate.id.UUIDGenerator"
	)
	  protected String id;
}
