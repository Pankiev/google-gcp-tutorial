package hello;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Random;

@SpringBootApplication
@RestController
public class Application {

  static class Self {
    public String href;
  }

  static class Links {
    public Self self;
  }

  static class PlayerState {
    public Integer x;
    public Integer y;
    public String direction;
    public Boolean wasHit;
    public Integer score;
  }

  static class Arena {
    public List<Integer> dims;
    public Map<String, PlayerState> state;
  }

  static class ArenaUpdate {
    public Links _links;
    public Arena arena;
  }

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @InitBinder
  public void initBinder(WebDataBinder binder) {
    binder.initDirectFieldAccess();
  }

  @GetMapping("/")
  public String index() {
    return "Let the battle begin!";
  }

  private static final Random random = new Random();

  @PostMapping("/**")
  public String index(@RequestBody ArenaUpdate arenaUpdate) {
    System.out.println(arenaUpdate);
    
    String selfHref = arenaUpdate._links.self.href;
    PlayerState myState = arenaUpdate.arena.state.get(selfHref);
    if((myState.wasHit && random.nextInt(2) == 1) || !hasTargetInRange(arenaUpdate)) {
        if(random.nextInt(3) == 1) {
            return "R";
        } else {
            return "F";
        }
    }
    return "T";
  }

  private boolean hasTargetInRange(ArenaUpdate arenaUpdate) {
    String selfHref = arenaUpdate._links.self.href;
    PlayerState myState = arenaUpdate.arena.state.get(selfHref);
    return (myState.direction.equals("W") && arenaUpdate.arena.state.values().stream().anyMatch(state -> state.y == myState.y && state.x >= myState.x - 3 && state.x < myState.x)) ||
    (myState.direction.equals("E") && arenaUpdate.arena.state.values().stream().anyMatch(state -> state.y == myState.y && state.x <= myState.x + 3 && state.x > myState.x)) ||
    (myState.direction.equals("N") && arenaUpdate.arena.state.values().stream().anyMatch(state -> state.y >= myState.y - 3 && state.y < myState.y && state.x == myState.x)) ||
    (myState.direction.equals("S") && arenaUpdate.arena.state.values().stream().anyMatch(state -> state.y <= myState.y + 3 && state.y > myState.y && state.x == myState.x));
  } 
}

