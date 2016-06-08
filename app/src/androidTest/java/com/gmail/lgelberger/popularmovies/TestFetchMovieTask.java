package com.gmail.lgelberger.popularmovies;

import android.test.AndroidTestCase;

/**
 * Delete???????? hard to test AsyncTask!
 *
 *
 * Created by Leslie on 2016-05-19.
 */
public class TestFetchMovieTask extends AndroidTestCase {

    //ContentValues[] bulkContentValues = TestUtilities.createMovieValues()


    void testOnPostExecute(){
            //first create String of JSON values

            //then
      //  FetchMoviesFromApiTask.onPost

    }


    String myJSONString = "{\n" +
            "   \"page\":1,\n" +
            "   \"results\":[\n" +
            "      {\n" +
            "         \"poster_path\":\"\\/inVq3FRqcYIRl2la8iZikYYxFNR.jpg\",\n" +
            "         \"adult\":false,\n" +
            "         \"overview\":\"Based upon Marvel Comics’ most unconventional anti-hero, DEADPOOL tells the origin story of former Special Forces operative turned mercenary Wade Wilson, who after being subjected to a rogue experiment that leaves him with accelerated healing powers, adopts the alter ego Deadpool. Armed with his new abilities and a dark, twisted sense of humor, Deadpool hunts down the man who nearly destroyed his life.\",\n" +
            "         \"release_date\":\"2016-02-09\",\n" +
            "         \"genre_ids\":[\n" +
            "            28,\n" +
            "            12,\n" +
            "            35\n" +
            "         ],\n" +
            "         \"id\":293660,\n" +
            "         \"original_title\":\"Deadpool\",\n" +
            "         \"original_language\":\"en\",\n" +
            "         \"title\":\"Deadpool\",\n" +
            "         \"backdrop_path\":\"\\/n1y094tVDFATSzkTnFxoGZ1qNsG.jpg\",\n" +
            "         \"popularity\":46.708915,\n" +
            "         \"vote_count\":1864,\n" +
            "         \"video\":false,\n" +
            "         \"vote_average\":7.24\n" +
            "      },\n" +
            "      {\n" +
            "         \"poster_path\":\"\\/kqjL17yufvn9OVLyXYpvtyrFfak.jpg\",\n" +
            "         \"adult\":false,\n" +
            "         \"overview\":\"An apocalyptic story set in the furthest reaches of our planet, in a stark desert landscape where humanity is broken, and most everyone is crazed fighting for the necessities of life. Within this world exist two rebels on the run who just might be able to restore order. There's Max, a man of action and a man of few words, who seeks peace of mind following the loss of his wife and child in the aftermath of the chaos. And Furiosa, a woman of action and a woman who believes her path to survival may be achieved if she can make it across the desert back to her childhood homeland.\",\n" +
            "         \"release_date\":\"2015-05-13\",\n" +
            "         \"genre_ids\":[\n" +
            "            878,\n" +
            "            53,\n" +
            "            28,\n" +
            "            12\n" +
            "         ],\n" +
            "         \"id\":76341,\n" +
            "         \"original_title\":\"Mad Max: Fury Road\",\n" +
            "         \"original_language\":\"en\",\n" +
            "         \"title\":\"Mad Max: Fury Road\",\n" +
            "         \"backdrop_path\":\"\\/tbhdm8UJAb4ViCTsulYFL3lxMCd.jpg\",\n" +
            "         \"popularity\":31.001775,\n" +
            "         \"vote_count\":4059,\n" +
            "         \"video\":false,\n" +
            "         \"vote_average\":7.43\n" +
            "      },\n" +
            "      {\n" +
            "         \"poster_path\":\"\\/fqe8JxDNO8B8QfOGTdjh6sPCdSC.jpg\",\n" +
            "         \"adult\":false,\n" +
            "         \"overview\":\"Bounty hunters seek shelter from a raging blizzard and get caught up in a plot of betrayal and deception.\",\n" +
            "         \"release_date\":\"2015-12-25\",\n" +
            "         \"genre_ids\":[\n" +
            "            18,\n" +
            "            9648,\n" +
            "            53,\n" +
            "            37\n" +
            "         ],\n" +
            "         \"id\":273248,\n" +
            "         \"original_title\":\"The Hateful Eight\",\n" +
            "         \"original_language\":\"en\",\n" +
            "         \"title\":\"The Hateful Eight\",\n" +
            "         \"backdrop_path\":\"\\/sSvgNBeBNzAuKl8U8sP50ETJPgx.jpg\",\n" +
            "         \"popularity\":25.323611,\n" +
            "         \"vote_count\":1172,\n" +
            "         \"video\":false,\n" +
            "         \"vote_average\":7.33\n" +
            "      },\n" +
            "      {\n" +
            "         \"poster_path\":\"\\/hE24GYddaxB9MVZl1CaiI86M3kp.jpg\",\n" +
            "         \"adult\":false,\n" +
            "         \"overview\":\"A cryptic message from Bond’s past sends him on a trail to uncover a sinister organization. While M battles political forces to keep the secret service alive, Bond peels back the layers of deceit to reveal the terrible truth behind SPECTRE.\",\n" +
            "         \"release_date\":\"2015-10-26\",\n" +
            "         \"genre_ids\":[\n" +
            "            28,\n" +
            "            12,\n" +
            "            80\n" +
            "         ],\n" +
            "         \"id\":206647,\n" +
            "         \"original_title\":\"Spectre\",\n" +
            "         \"original_language\":\"en\",\n" +
            "         \"title\":\"Spectre\",\n" +
            "         \"backdrop_path\":\"\\/wVTYlkKPKrljJfugXN7UlLNjtuJ.jpg\",\n" +
            "         \"popularity\":19.985216,\n" +
            "         \"vote_count\":2237,\n" +
            "         \"video\":false,\n" +
            "         \"vote_average\":6.27\n" +
            "      },\n" +
            "      {\n" +
            "         \"poster_path\":\"\\/jjBgi2r5cRt36xF6iNUEhzscEcb.jpg\",\n" +
            "         \"adult\":false,\n" +
            "         \"overview\":\"Twenty-two years after the events of Jurassic Park, Isla Nublar now features a fully functioning dinosaur theme park, Jurassic World, as originally envisioned by John Hammond.\",\n" +
            "         \"release_date\":\"2015-06-09\",\n" +
            "         \"genre_ids\":[\n" +
            "            28,\n" +
            "            12,\n" +
            "            878,\n" +
            "            53\n" +
            "         ],\n" +
            "         \"id\":135397,\n" +
            "         \"original_title\":\"Jurassic World\",\n" +
            "         \"original_language\":\"en\",\n" +
            "         \"title\":\"Jurassic World\",\n" +
            "         \"backdrop_path\":\"\\/dkMD5qlogeRMiEixC4YNPUvax2T.jpg\",\n" +
            "         \"popularity\":19.739052,\n" +
            "         \"vote_count\":4072,\n" +
            "         \"video\":false,\n" +
            "         \"vote_average\":6.68\n" +
            "      },\n" +
            "      {\n" +
            "         \"poster_path\":\"\\/pKop1BcfgHzaN26EnhqLmjVP6LQ.jpg\",\n" +
            "         \"adult\":false,\n" +
            "         \"overview\":\"In the animal city of Zootopia, a fast-talking fox who's trying to make it big goes on the run when he's framed for a crime he didn't commit. Zootopia's top cop, a self-righteous rabbit, is hot on his tail, but when both become targets of a conspiracy, they're forced to team up and discover even natural enemies can become best friends.\",\n" +
            "         \"release_date\":\"2016-02-12\",\n" +
            "         \"genre_ids\":[\n" +
            "            35,\n" +
            "            16,\n" +
            "            28,\n" +
            "            12,\n" +
            "            10751\n" +
            "         ],\n" +
            "         \"id\":269149,\n" +
            "         \"original_title\":\"Zootopia\",\n" +
            "         \"original_language\":\"en\",\n" +
            "         \"title\":\"Zootopia\",\n" +
            "         \"backdrop_path\":\"\\/mhdeE1yShHTaDbJVdWyTlzFvNkr.jpg\",\n" +
            "         \"popularity\":19.272315,\n" +
            "         \"vote_count\":220,\n" +
            "         \"video\":false,\n" +
            "         \"vote_average\":7.2\n" +
            "      },\n" +
            "      {\n" +
            "         \"poster_path\":\"\\/5W794ugjRwYx6IdFp1bXJqqMWRg.jpg\",\n" +
            "         \"adult\":false,\n" +
            "         \"overview\":\"In the 1820s, a frontiersman, Hugh Glass, sets out on a path of vengeance against those who left him for dead after a bear mauling.\",\n" +
            "         \"release_date\":\"2015-12-25\",\n" +
            "         \"genre_ids\":[\n" +
            "            37,\n" +
            "            18,\n" +
            "            12,\n" +
            "            53\n" +
            "         ],\n" +
            "         \"id\":281957,\n" +
            "         \"original_title\":\"The Revenant\",\n" +
            "         \"original_language\":\"en\",\n" +
            "         \"title\":\"The Revenant\",\n" +
            "         \"backdrop_path\":\"\\/uS1SkjVviraGfFNgkDwe7ohTm8B.jpg\",\n" +
            "         \"popularity\":18.895198,\n" +
            "         \"vote_count\":1892,\n" +
            "         \"video\":false,\n" +
            "         \"vote_average\":7.28\n" +
            "      },\n" +
            "      {\n" +
            "         \"poster_path\":\"\\/yTdTuJww8NnL9YLaxL2LxDG5uQ7.jpg\",\n" +
            "         \"adult\":false,\n" +
            "         \"overview\":\"A common thief joins a mythical god on a quest through Egypt.\",\n" +
            "         \"release_date\":\"2016-02-25\",\n" +
            "         \"genre_ids\":[\n" +
            "            28,\n" +
            "            12,\n" +
            "            14\n" +
            "         ],\n" +
            "         \"id\":205584,\n" +
            "         \"original_title\":\"Gods of Egypt\",\n" +
            "         \"original_language\":\"en\",\n" +
            "         \"title\":\"Gods of Egypt\",\n" +
            "         \"backdrop_path\":\"\\/2s7uzOHQtOcP2fSoM9Loh4QKiTJ.jpg\",\n" +
            "         \"popularity\":17.818027,\n" +
            "         \"vote_count\":114,\n" +
            "         \"video\":false,\n" +
            "         \"vote_average\":4.44\n" +
            "      },\n" +
            "      {\n" +
            "         \"poster_path\":\"\\/fYzpM9GmpBlIC893fNjoWCwE24H.jpg\",\n" +
            "         \"adult\":false,\n" +
            "         \"overview\":\"Thirty years after defeating the Galactic Empire, Han Solo and his allies face a new threat from the evil Kylo Ren and his army of Stormtroopers.\",\n" +
            "         \"release_date\":\"2015-12-15\",\n" +
            "         \"genre_ids\":[\n" +
            "            28,\n" +
            "            12,\n" +
            "            878,\n" +
            "            14\n" +
            "         ],\n" +
            "         \"id\":140607,\n" +
            "         \"original_title\":\"Star Wars: The Force Awakens\",\n" +
            "         \"original_language\":\"en\",\n" +
            "         \"title\":\"Star Wars: The Force Awakens\",\n" +
            "         \"backdrop_path\":\"\\/njv65RTipNSTozFLuF85jL0bcQe.jpg\",\n" +
            "         \"popularity\":17.501313,\n" +
            "         \"vote_count\":3287,\n" +
            "         \"video\":false,\n" +
            "         \"vote_average\":7.72\n" +
            "      },\n" +
            "      {\n" +
            "         \"poster_path\":\"\\/t5tGykRvvlLBULIPsAJEzGg1ylm.jpg\",\n" +
            "         \"adult\":false,\n" +
            "         \"overview\":\"A father is without the means to pay for his daughter's medical treatment. As a last resort, he partners with a greedy co-worker to rob a casino. When things go awry they're forced to hijack a city bus.\",\n" +
            "         \"release_date\":\"2015-11-13\",\n" +
            "         \"genre_ids\":[\n" +
            "            28,\n" +
            "            53\n" +
            "         ],\n" +
            "         \"id\":336004,\n" +
            "         \"original_title\":\"Heist\",\n" +
            "         \"original_language\":\"en\",\n" +
            "         \"title\":\"Heist\",\n" +
            "         \"backdrop_path\":\"\\/cBlnfR0n1GA2vPoUQNcbL9pb3VW.jpg\",\n" +
            "         \"popularity\":17.284706,\n" +
            "         \"vote_count\":117,\n" +
            "         \"video\":false,\n" +
            "         \"vote_average\":5.48\n" +
            "      },\n" +
            "      {\n" +
            "         \"poster_path\":\"\\/5aGhaIHYuQbqlHWvWYqMCnj40y2.jpg\",\n" +
            "         \"adult\":false,\n" +
            "         \"overview\":\"During a manned mission to Mars, Astronaut Mark Watney is presumed dead after a fierce storm and left behind by his crew. But Watney has survived and finds himself stranded and alone on the hostile planet. With only meager supplies, he must draw upon his ingenuity, wit and spirit to subsist and find a way to signal to Earth that he is alive.\",\n" +
            "         \"release_date\":\"2015-09-30\",\n" +
            "         \"genre_ids\":[\n" +
            "            18,\n" +
            "            12,\n" +
            "            878\n" +
            "         ],\n" +
            "         \"id\":286217,\n" +
            "         \"original_title\":\"The Martian\",\n" +
            "         \"original_language\":\"en\",\n" +
            "         \"title\":\"The Martian\",\n" +
            "         \"backdrop_path\":\"\\/sy3e2e4JwdAtd2oZGA2uUilZe8j.jpg\",\n" +
            "         \"popularity\":17.187386,\n" +
            "         \"vote_count\":2916,\n" +
            "         \"video\":false,\n" +
            "         \"vote_average\":7.58\n" +
            "      },\n" +
            "      {\n" +
            "         \"poster_path\":\"\\/5JU9ytZJyR3zmClGmVm9q4Geqbd.jpg\",\n" +
            "         \"adult\":false,\n" +
            "         \"overview\":\"The year is 2029. John Connor, leader of the resistance continues the war against the machines. At the Los Angeles offensive, John's fears of the unknown future begin to emerge when TECOM spies reveal a new plot by SkyNet that will attack him from both fronts; past and future, and will ultimately change warfare forever.\",\n" +
            "         \"release_date\":\"2015-06-23\",\n" +
            "         \"genre_ids\":[\n" +
            "            878,\n" +
            "            28,\n" +
            "            53,\n" +
            "            12\n" +
            "         ],\n" +
            "         \"id\":87101,\n" +
            "         \"original_title\":\"Terminator Genisys\",\n" +
            "         \"original_language\":\"en\",\n" +
            "         \"title\":\"Terminator Genisys\",\n" +
            "         \"backdrop_path\":\"\\/bIlYH4l2AyYvEysmS2AOfjO7Dn8.jpg\",\n" +
            "         \"popularity\":16.819975,\n" +
            "         \"vote_count\":1862,\n" +
            "         \"video\":false,\n" +
            "         \"vote_average\":6.05\n" +
            "      },\n" +
            "      {\n" +
            "         \"poster_path\":\"\\/nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg\",\n" +
            "         \"adult\":false,\n" +
            "         \"overview\":\"Interstellar chronicles the adventures of a group of explorers who make use of a newly discovered wormhole to surpass the limitations on human space travel and conquer the vast distances involved in an interstellar voyage.\",\n" +
            "         \"release_date\":\"2014-11-05\",\n" +
            "         \"genre_ids\":[\n" +
            "            12,\n" +
            "            18,\n" +
            "            878\n" +
            "         ],\n" +
            "         \"id\":157336,\n" +
            "         \"original_title\":\"Interstellar\",\n" +
            "         \"original_language\":\"en\",\n" +
            "         \"title\":\"Interstellar\",\n" +
            "         \"backdrop_path\":\"\\/xu9zaAevzQ5nnrsXN6JcahLnG4i.jpg\",\n" +
            "         \"popularity\":15.192744,\n" +
            "         \"vote_count\":4550,\n" +
            "         \"video\":false,\n" +
            "         \"vote_average\":8.21\n" +
            "      },\n" +
            "      {\n" +
            "         \"poster_path\":\"\\/nN4cEJMHJHbJBsp3vvvhtNWLGqg.jpg\",\n" +
            "         \"adult\":false,\n" +
            "         \"overview\":\"With the nation of Panem in a full scale war, Katniss confronts President Snow in the final showdown. Teamed with a group of her closest friends – including Gale, Finnick, and Peeta – Katniss goes off on a mission with the unit from District 13 as they risk their lives to stage an assassination attempt on President Snow who has become increasingly obsessed with destroying her. The mortal traps, enemies, and moral choices that await Katniss will challenge her more than any arena she faced in The Hunger Games.\",\n" +
            "         \"release_date\":\"2015-11-18\",\n" +
            "         \"genre_ids\":[\n" +
            "            12,\n" +
            "            28,\n" +
            "            18\n" +
            "         ],\n" +
            "         \"id\":131634,\n" +
            "         \"original_title\":\"The Hunger Games: Mockingjay - Part 2\",\n" +
            "         \"original_language\":\"en\",\n" +
            "         \"title\":\"The Hunger Games: Mockingjay - Part 2\",\n" +
            "         \"backdrop_path\":\"\\/qjn3fzCAHGfl0CzeUlFbjrsmu4c.jpg\",\n" +
            "         \"popularity\":15.150508,\n" +
            "         \"vote_count\":1135,\n" +
            "         \"video\":false,\n" +
            "         \"vote_average\":6.9\n" +
            "      },\n" +
            "      {\n" +
            "         \"poster_path\":\"\\/cWERd8rgbw7bCMZlwP207HUXxym.jpg\",\n" +
            "         \"adult\":false,\n" +
            "         \"overview\":\"Katniss Everdeen reluctantly becomes the symbol of a mass rebellion against the autocratic Capitol.\",\n" +
            "         \"release_date\":\"2014-11-19\",\n" +
            "         \"genre_ids\":[\n" +
            "            878,\n" +
            "            12,\n" +
            "            53\n" +
            "         ],\n" +
            "         \"id\":131631,\n" +
            "         \"original_title\":\"The Hunger Games: Mockingjay - Part 1\",\n" +
            "         \"original_language\":\"en\",\n" +
            "         \"title\":\"The Hunger Games: Mockingjay - Part 1\",\n" +
            "         \"backdrop_path\":\"\\/83nHcz2KcnEpPXY50Ky2VldewJJ.jpg\",\n" +
            "         \"popularity\":13.254175,\n" +
            "         \"vote_count\":2610,\n" +
            "         \"video\":false,\n" +
            "         \"vote_average\":6.83\n" +
            "      },\n" +
            "      {\n" +
            "         \"poster_path\":\"\\/rSZs93P0LLxqlVEbI001UKoeCQC.jpg\",\n" +
            "         \"adult\":false,\n" +
            "         \"overview\":\"A fading actor best known for his portrayal of a popular superhero attempts to mount a comeback by appearing in a Broadway play. As opening night approaches, his attempts to become more altruistic, rebuild his career, and reconnect with friends and family prove more difficult than expected.\",\n" +
            "         \"release_date\":\"2014-10-17\",\n" +
            "         \"genre_ids\":[\n" +
            "            18,\n" +
            "            35\n" +
            "         ],\n" +
            "         \"id\":194662,\n" +
            "         \"original_title\":\"Birdman\",\n" +
            "         \"original_language\":\"en\",\n" +
            "         \"title\":\"Birdman\",\n" +
            "         \"backdrop_path\":\"\\/hUDEHvhNJLNcb83Pp7xnFn0Wj09.jpg\",\n" +
            "         \"popularity\":13.188759,\n" +
            "         \"vote_count\":2096,\n" +
            "         \"video\":false,\n" +
            "         \"vote_average\":7.34\n" +
            "      },\n" +
            "      {\n" +
            "         \"poster_path\":\"\\/ngKxbvsn9Si5TYVJfi1EGAGwThU.jpg\",\n" +
            "         \"adult\":false,\n" +
            "         \"overview\":\"The true story of how The Boston Globe uncovered the massive scandal of child molestation and cover-up within the local Catholic Archdiocese, shaking the entire Catholic Church to its core.\",\n" +
            "         \"release_date\":\"2015-11-06\",\n" +
            "         \"genre_ids\":[\n" +
            "            18,\n" +
            "            36,\n" +
            "            53\n" +
            "         ],\n" +
            "         \"id\":314365,\n" +
            "         \"original_title\":\"Spotlight\",\n" +
            "         \"original_language\":\"en\",\n" +
            "         \"title\":\"Spotlight\",\n" +
            "         \"backdrop_path\":\"\\/t3Oea7KbSpOvuqddMnlFtZ4WHn.jpg\",\n" +
            "         \"popularity\":13.165539,\n" +
            "         \"vote_count\":610,\n" +
            "         \"video\":false,\n" +
            "         \"vote_average\":7.71\n" +
            "      },\n" +
            "      {\n" +
            "         \"poster_path\":\"\\/y31QB9kn3XSudA15tV7UWQ9XLuW.jpg\",\n" +
            "         \"adult\":false,\n" +
            "         \"overview\":\"Light years from Earth, 26 years after being abducted, Peter Quill finds himself the prime target of a manhunt after discovering an orb wanted by Ronan the Accuser.\",\n" +
            "         \"release_date\":\"2014-07-30\",\n" +
            "         \"genre_ids\":[\n" +
            "            878,\n" +
            "            14,\n" +
            "            12\n" +
            "         ],\n" +
            "         \"id\":118340,\n" +
            "         \"original_title\":\"Guardians of the Galaxy\",\n" +
            "         \"original_language\":\"en\",\n" +
            "         \"title\":\"Guardians of the Galaxy\",\n" +
            "         \"backdrop_path\":\"\\/bHarw8xrmQeqf3t8HpuMY7zoK4x.jpg\",\n" +
            "         \"popularity\":12.14564,\n" +
            "         \"vote_count\":4278,\n" +
            "         \"video\":false,\n" +
            "         \"vote_average\":8.03\n" +
            "      },\n" +
            "      {\n" +
            "         \"poster_path\":\"\\/lIv1QinFqz4dlp5U4lQ6HaiskOZ.jpg\",\n" +
            "         \"adult\":false,\n" +
            "         \"overview\":\"Under the direction of a ruthless instructor, a talented young drummer begins to pursue perfection at any cost, even his humanity.\",\n" +
            "         \"release_date\":\"2014-10-10\",\n" +
            "         \"genre_ids\":[\n" +
            "            18,\n" +
            "            10402\n" +
            "         ],\n" +
            "         \"id\":244786,\n" +
            "         \"original_title\":\"Whiplash\",\n" +
            "         \"original_language\":\"en\",\n" +
            "         \"title\":\"Whiplash\",\n" +
            "         \"backdrop_path\":\"\\/6bbZ6XyvgfjhQwbplnUh1LSj1ky.jpg\",\n" +
            "         \"popularity\":11.873337,\n" +
            "         \"vote_count\":1703,\n" +
            "         \"video\":false,\n" +
            "         \"vote_average\":8.36\n" +
            "      },\n" +
            "      {\n" +
            "         \"poster_path\":\"\\/vgAHvS0bT3fpcpnJqT6uDTUsHTo.jpg\",\n" +
            "         \"adult\":false,\n" +
            "         \"overview\":\"Immediately after the events of The Desolation of Smaug, Bilbo and the dwarves try to defend Erebor's mountain of treasure from others who claim it: the men of the ruined Laketown and the elves of Mirkwood. Meanwhile an army of Orcs led by Azog the Defiler is marching on Erebor, fueled by the rise of the dark lord Sauron. Dwarves, elves and men must unite, and the hope for Middle-Earth falls into Bilbo's hands.\",\n" +
            "         \"release_date\":\"2014-12-10\",\n" +
            "         \"genre_ids\":[\n" +
            "            12,\n" +
            "            14\n" +
            "         ],\n" +
            "         \"id\":122917,\n" +
            "         \"original_title\":\"The Hobbit: The Battle of the Five Armies\",\n" +
            "         \"original_language\":\"en\",\n" +
            "         \"title\":\"The Hobbit: The Battle of the Five Armies\",\n" +
            "         \"backdrop_path\":\"\\/qhH3GyIfAnGv1pjdV3mw03qAilg.jpg\",\n" +
            "         \"popularity\":11.635063,\n" +
            "         \"vote_count\":2495,\n" +
            "         \"video\":false,\n" +
            "         \"vote_average\":7.13\n" +
            "      }\n" +
            "   ],\n" +
            "   \"total_results\":258485,\n" +
            "   \"total_pages\":12925\n" +
            "}";



}
