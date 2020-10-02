import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "bucket")
public class Bucket {

  @Id
  public String id;
  
  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "bucket_id")
  public List<Plop> plops = new ArrayList<>();
  
  public Bucket() {
    this.id = UUID.randomUUID().toString();
  }
}
