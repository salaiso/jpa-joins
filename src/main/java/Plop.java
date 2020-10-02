import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "plop")
public class Plop {
  
  @Id
  public String id;
  
  public String content;
  
  public String bucket_id;
  
  public Plop() {
  }
  
  public Plop(String content) {
    this.id = UUID.randomUUID().toString();
    this.content = content;
  }
}
