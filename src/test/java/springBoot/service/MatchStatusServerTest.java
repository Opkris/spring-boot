package springBoot.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import springBoot.entity.MatchStats;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class MatchStatusServerTest extends ServiceTestBase {

    @Autowired
    private MatchStatsService matchStatsService;

    @Autowired
    private UserService userService;

    @Test
    public void testDefaultStats(){

        String username = "Im_A_User";

        userService.createUser(username, "1337_OP");

        MatchStats stats = matchStatsService.getMatchStats(username);

        assertEquals(0, (int) stats.getVictories());
        assertEquals(0, (int) stats.getDefeats());
    }

    @Test
    public void testStats(){

        String username = "new_User";

        userService.createUser(username, "lolz42");

        matchStatsService.reportVictory(username);
        matchStatsService.reportVictory(username);
        matchStatsService.reportVictory(username);
        matchStatsService.reportDefeat(username);
        matchStatsService.reportDefeat(username);

        MatchStats stats = matchStatsService.getMatchStats(username);

        assertEquals(3, (int) stats.getVictories());
        assertEquals(2, (int) stats.getDefeats());
    }
}
