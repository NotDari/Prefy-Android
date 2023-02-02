package com.example.prefy.old_methods;

import android.view.View;
import android.widget.Toast;

import com.example.prefy.Activities.MainActivity;
import com.example.prefy.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AuthenticationBackup {
    /**
    private void initTemp(){
        String tempString = "dronmalyutin@yandex.ru\n" +
                "15 Nov 2017\n" +
                "15 Nov 2017\n" +
                "wYiHDl7P0xPvQ7qJIcl5GtBC9hf1\n" +
                "anastasia.susharina@yandex.ru\n" +
                "15 Nov 2017\n" +
                "15 Nov 2017\n" +
                "LXr2tonVEwVJL1uakmmycSb4i0g2\n" +
                "urmaev-nikita@mail.ru\n" +
                "15 Nov 2017\n" +
                "15 Nov 2017\n" +
                "b12mI7GTF5bkrztk0ShnFW1WJDX2\n" +
                "axenov7@yandex.ru\n" +
                "15 Nov 2017\n" +
                "15 Nov 2017\n" +
                "Y7PcVoEebugyZfxTXDTKb6KzpzJ3\n" +
                "danila.komarov@gmail.com\n" +
                "15 Nov 2017\n" +
                "15 Nov 2017\n" +
                "Nk6R6UkXP1RDwS3N40VXyiTP1no1\n" +
                "averindaniilvyacheslavovich@gmail.com\n" +
                "15 Nov 2017\n" +
                "15 Nov 2017\n" +
                "knYr5qKZNahkyg9SrlHtFYBNlVF3\n" +
                "tural.tagiev2002@mail.ru\n" +
                "15 Nov 2017\n" +
                "15 Nov 2017\n" +
                "FIVdAm16MWTXnTCphpEVZwDyjxi1\n" +
                "chipa1994@mail.ru\n" +
                "15 Nov 2017\n" +
                "15 Nov 2017\n" +
                "7DQuuX5IFwUqjvLjORlRMda9WJ72\n" +
                "snfxcs@gmail.com\n" +
                "15 Nov 2017\n" +
                "15 Nov 2017\n" +
                "zId2dmSW7SUho2blQqTkmeIzSpl1\n" +
                "forumden@icloud.com\n" +
                "15 Nov 2017\n" +
                "15 Nov 2017\n" +
                "8fvWWZMHY9byrKuFOvBHWjn3iD52\n" +
                "merkuri.pavel@yandex.ru\n" +
                "15 Nov 2017\n" +
                "15 Nov 2017\n" +
                "rm76wcJMPqTQkXzP2lZg94IA1r23\n" +
                "dulygaeva@mail.ru\n" +
                "15 Nov 2017\n" +
                "15 Nov 2017\n" +
                "i9ZwrmjL7vdFmy7ziK67ZZMeK862\n" +
                "svetlana.vaiman@yandex.ru\n" +
                "15 Nov 2017\n" +
                "23 Sept 2018\n" +
                "5PsuRzriMXUja3AuLndcimWZ1ub2\n" +
                "maxmeb@icloud.com\n" +
                "15 Nov 2017\n" +
                "15 Nov 2017\n" +
                "Q1e6gt7WV0QTNHH85J6raVj0Iq12\n" +
                "angelko98@mail.ru\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "rHSLRRPXJRZYeZG8oXAZ20XCE0R2\n" +
                "lesha.nelog@gmail.com\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "tkoS2x7hsqa3qzz5x6WRp0aGFYC2\n" +
                "alexey.nechai@ya.ru\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "azw4ksZwe2hBv52sD1OBwICCtZU2\n" +
                "dimo-dmitriev_97@mail.ru\n" +
                "14 Nov 2017\n" +
                "6 Jan 2018\n" +
                "YEVWN3GQ4CPu97GYi8N0KAUgLLU2\n" +
                "lukindima1998@mail.ru\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "LzrULNeLU6YBJMUq1XamBKs6SZh1\n" +
                "levix.business@gmail.com\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "Lez7mebx3vMxDnhHPDrB88ME7Al2\n" +
                "ollypo13@gmail.com\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "MRouULGKRBd8vq9prXbf7WIZOIr1\n" +
                "zimmer483@bk.ru\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "cHcUb4POsVRKVlayDkEOVmS6Hwb2\n" +
                "lashuto4ka@yandex.ru\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "PrQiYXAfllhGbpNRaQ9bPJyrFfw2\n" +
                "kastsiura@icloud.com\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "gTvXJB335cPqiaZmPNj9fEreYls2\n" +
                "d.kondratovitch@yandex.ru\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "mZ1nlBetVAPY5qWiTHNMiTwcrLe2\n" +
                "colaboy@eaglarts.com\n" +
                "14 Nov 2017\n" +
                "19 Nov 2017\n" +
                "r288Q7Jjw9Vx8EqGAu2rw9qZjcd2\n" +
                "anime777.59@mail.ru\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "ScC1X5s5S7TST4EBiGebMTb3YSQ2\n" +
                "mr.deathstar@mail.ru\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "rKBBf8qVjNWYDFrDApHGjVzGTsT2\n" +
                "dima1999xxx@mail.ru\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "nWw8ZKVBbQPrtqHpFF1uH3XEA0x1\n" +
                "gandilyan19980905@mail.ru\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "tgbROiIpAQTrcJitwLqYzxnnHao2\n" +
                "cheazz428@gmail.com\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "MvMWcJZqZWSBYYl39IJFfPbnabg2\n" +
                "n.opekun@icloud.com\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "SN42SQFbKMSXP6SEJ9gELoz0eRf2\n" +
                "begemot1999@inbox.ru\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "PvsFSgrpSpNSBk0qQuOIi0WgMaR2\n" +
                "viprudnikov@list.ru\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "3jeWB7cKbdgF9HZUekcoyRSHUE22\n" +
                "arr.shewchuck@yandex.ru\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "fZKe8dPaZ9U3Kg7K9UVSHCI3hMI3\n" +
                "kiroseeker@mail.ru\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "ks3BKaoEOWVl7dKvQMXTshZ4mzj1\n" +
                "sign.cloud@icloud.com\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "ndihnXc8MNZVdbpd0WdfkcqDnVm1\n" +
                "sgureshidze@inbox.ru\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "RmkYFbsP6tTbkkwYQ2NKIWvdPXl2\n" +
                "ibonezachem@mail.ru\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "75cfolmzc9PMkoKQagr22fa3kS73\n" +
                "kataylova@yandex.ru\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "0IdL4bXVBkSpGlmyM8Af2FElaah2\n" +
                "desh_rool@inbox.ru\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "2wfcalJqGkShGC6npLK0I5euXV42\n" +
                "muid05@mail.ru\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "q76JZyJYJ2MljP0j81hSIAUmtL32\n" +
                "egorgritsko@yandex.ru\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "hCVLSGJkPdf45s5r2pe02VAFvq82\n" +
                "elomanrage9@gmail.com\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "wUDL6lfSq7VhCFHPzSuJy7Lv4Px2\n" +
                "obersky@list.ru\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "ADyZHUFBj8NIgbg4BUCrhddigVb2\n" +
                "bars131997@mail.ru\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "ZJadBdjyUTSndhzca1EoFTMdO6o1\n" +
                "snaker399@gmail.com\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "x5ncpaJM8ugH35sIZH8T5oBZuhi2\n" +
                "viviana@bk.ru\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "0U3OKXfEexRe1aLzURXSCs2AaKx1\n" +
                "mitalidd@mail.ru\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "BElaHFhVcrQu3ZxCAlnX3Ornjjh1\n" +
                "henrygame@eaglarts.com\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "8K23bYvJxYO2a2RgK7ygO591gmh1\n" +
                "vano997@yandex.ru\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "Xu3fWGHyPwgjhjBuoL00nrAhggl2\n" +
                "lodek@mail.ru\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "mmrpf8bj8Agz0fZtQMSu2tXdHho2\n" +
                "tamamyan41@gmail.com\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "6JR0cUW27BYXTmZWSwbxXLIgodY2\n" +
                "angellina99@icloud.com\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "Fya1hNpZvBYXT0izUii6U6Aj6sP2\n" +
                "nastya199847@gmail.con\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "5jkCHjHY0WW87ouL15QJ75OTKs73\n" +
                "hiphoperivan@gmail.com\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "T671e5rDWuP3UzAF8G1oYCoL0nj2\n" +
                "valerijavolkova8@mail.ru\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "swtOlIUNaYQazLWV8aSIPYEbSCF2\n" +
                "huyganeji20@mai.ru\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "RAUxVr5q4lT9VVI9jzVoMG3NcS12\n" +
                "kretyuk97@yandex.ru\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "0hWZZoaQYnfVVwiKMQQX2nZg2Kg1\n" +
                "mashka@eaglarts.com\n" +
                "14 Nov 2017\n" +
                "26 Nov 2017\n" +
                "ew0SH2o6VDYZTwEh9bDFxrM3ZSz2\n" +
                "polina.kozina.77@mail.ru\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "8DViODFqf6SVQ0JaR5z6QbC7v503\n" +
                "ludoch@eaglarts.com\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "9s4P6bU7nGRnQxF0WAThHF1VzNq1\n" +
                "chvirinamaria@gmail.com\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "GhIxyo6YScOTKHgnN7Awxn2TCHf1\n" +
                "murtuzalievshama@gmail.com\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "JuH2lVV8AdfcNIl3d2PWAabjao53\n" +
                "lehno26@gmail.com\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "uLBMzaXTPvMw2RM7N9o7uE7H2At2\n" +
                "primeworld12345@mail.ry\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "vjYvyyn5OpSsjol4jpJtFYIcftE3\n" +
                "niik686@mail.ru\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "1jhbIMyDu4N7Zo2SFPPgxd5tnBn1\n" +
                "jellytime@eaglarts.com\n" +
                "14 Nov 2017\n" +
                "15 Nov 2017\n" +
                "o8zjt7gwlsfPUXwMZLS3IcnA1e22\n" +
                "kirillka@eaglarts.com\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "aFhNIfBWr5NCNDpFknfD2lsb9MO2\n" +
                "pet55va@yandex.ru\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "F0r9HWCY3OQQxqpiAvBu9QEjFaJ3\n" +
                "roma.kholt@mail.ru\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "T0A3Y5ilfpOrjfeygg8K3wOsKOl2\n" +
                "iarut@icloud.com\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "OYWCUXC5yxN2aKL2XkYqIOz8ryp1\n" +
                "nastyushka-06@mail.ru\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "C36eK9ggOAXiFGz507H83EL1wku2\n" +
                "savagept.19@gmail.com\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "tXOT4CJ8bBdNHkqbI2Janj6qZo43\n" +
                "tori.justies.00@mail.ru\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "1zVo6h3G5wfNL481KMeo243LIPu2\n" +
                "andrew@eaglarts.com\n" +
                "14 Nov 2017\n" +
                "8 Dec 2017\n" +
                "lXBTXUaIdYcfVKoLvytdeI7QnJ42\n" +
                "nikol200@mail.ru\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "q5xZCUBZHjYIPoUwsH04clcPXJE2\n" +
                "silly@eaglarts.com\n" +
                "14 Nov 2017\n" +
                "4 Dec 2017\n" +
                "QmswXrUpDjRQuR5AH4R9e6cMQzc2\n" +
                "a.vdvnk@mail.ru\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "rpk53LGxwbSKBCf58uNkrIUt8Nl1\n" +
                "kat95@mail.ru\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "9F7FSw4U2KXvY1CRxfMoorEeiTS2\n" +
                "mashabelikina111546@yandex.ru\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "3OD0HxYY9RQbMss5u5hA5ZLrd1k1\n" +
                "pps_97@list.ru\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "PGzI7VJSyyLC3EodN9pYWNfqh2S2\n" +
                "loni1996@mail.ru\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "3wroZiNBMESftVuQmPJRZcvHYqp2\n" +
                "mariya.elizova@mail.ru\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "dpZj7m39XdXI1ALDAdAqThaTNuw1\n" +
                "ponomaryova.s@yandex.ru\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "VOLHNgQMDRQqoskwrZhp3E6bScj1\n" +
                "spidersy@eaglarts.com\n" +
                "14 Nov 2017\n" +
                "19 Nov 2017\n" +
                "nhFhCWy4UpVNClhPFcEzNsGUMkt2\n" +
                "tchu69@mail.ru\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "PesupIgiEPcmlOajEV3evinQt2j2\n" +
                "devlikamova96@gmail.com\n" +
                "14 Nov 2017\n" +
                "15 Nov 2017\n" +
                "9LPHlzdsDITA3gM4woBLLiHGQwB3\n" +
                "svlad_77@mail.ru\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "XfQUCYUdEdMVfYJ1tfro4FXtBpz2\n" +
                "piepie@eaglarts.com\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "P7buYSfGP4XkYNbUD9ovFtiLmr92\n" +
                "expleypc@gmail.com\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "cYGfSgwZ97bNBeOUoEbbxAafyzr2\n" +
                "jelly@eaglarts.com\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "64gQZSIocNU9HDBrsjAkD8PqPgs1\n" +
                "britousova2014@gmail.com\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "ZQk8ui0NvNNUCAwpbLw51AIzrW02\n" +
                "asliya0998@gmail.com\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "VFa5DKHLfUPjCgBOuvvRnb7qyHG3\n" +
                "gerasimova.nast2015@yandex.ru\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "RDtVz4qz6OgtyMqxdlOo82DA8D12\n" +
                "danil5757557@gmail.com\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "ETZiAveIPASyDXadGHZRFNX0L7F3\n" +
                "frolov_2015@inbox.ru\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "U0uPwtwAEUaZJ7g2ZJnTTZGT5eF2\n" +
                "iasher_9@inbox.ru\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "F4f9X7sBoeXAueBnI5t9lkJBDMD3\n" +
                "polya@eaglarts.com\n" +
                "14 Nov 2017\n" +
                "31 Jan 2018\n" +
                "uh5iztjhZdaCk44gDH8sFw2EfjV2\n" +
                "lasty000@mail.ru\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "wL5OLmlwmcQiy8EUAYpRtmItr2b2\n" +
                "chashkov-yura@yandex.ru\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "Wh4YmVeEbyMxIboaqQ5hxY781Jt2\n" +
                "lub.kochneva@yandex.ru\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "WGYomo8g1fbHt5RGnYRxTQnxvVz1\n" +
                "ivan_8d@ukr.net\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "GVDJvp4UnJVqXLEh59RckSTpTxv1\n" +
                "kingy@eaglarts.com\n" +
                "14 Nov 2017\n" +
                "23 Nov 2017\n" +
                "LCzTx2HGchZT4HYTv8ad6ck8bAZ2\n" +
                "legends_pro_centr@mail.ru\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "LRwZ3Ldd7HgDQQP4wtRv9KT0TRx2\n" +
                "sm.rem@yandex.ru\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "k8XOLROF5HWtDFIK05Up56R4wm52\n" +
                "jeidy@eaglarts.com\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "QHDmN9FLW3hl6M7eNCgYkbTkhz42\n" +
                "dasha@eaglarts.com\n" +
                "14 Nov 2017\n" +
                "16 Nov 2017\n" +
                "WuZyssu7n2OOR854KQMit8lSUG62\n" +
                "theninecreator@gmail.com\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "TQGXxrYBSreGbgzWn7isxb8L7f23\n" +
                "booky@eaglarts.com\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "G8zlp9usAoVnzLazdZM114vqACl2\n" +
                "www.mea123@mail.ru\n" +
                "14 Nov 2017\n" +
                "14 Nov 2017\n" +
                "1bfNgWqdF6grur1hQFqdT0maHMG3\n" +
                "lemongirl@eaglarts.com\n" +
                "13 Nov 2017\n" +
                "16 Nov 2017\n" +
                "qbcodWNrT6QjcnkW0dyTrL7dx4u1\n" +
                "beautiful@eaglarts.com\n" +
                "13 Nov 2017\n" +
                "13 Nov 2017\n" +
                "EIDDlpei9jTCfnXiqnFvDB0fDYI3\n" +
                "darthvader@eaglarts.com\n" +
                "13 Nov 2017\n" +
                "7 Dec 2017\n" +
                "2CzengDS7KglmklPv86SvKblxUg1\n" +
                "myway@eaglarts.com\n" +
                "13 Nov 2017\n" +
                "13 Nov 2017\n" +
                "V4YOgy8j3HdW88CcqEVbsSpk6Q02\n" +
                "mood@eaglarts.com\n" +
                "13 Nov 2017\n" +
                "20 Nov 2017\n" +
                "E5E2TgM64YVB9Qa0MMcz0OjTqJP2\n" +
                "danemon9@yandex.ru\n" +
                "13 Nov 2017\n" +
                "1 Apr 2018\n" +
                "5n2CHw8sPGdilZL6X6URSYmoBgs2\n" +
                "famous@eaglarts.com\n" +
                "13 Nov 2017\n" +
                "13 Nov 2017\n" +
                "uMC49thzVJRPatehaHaxCxEkKEF2\n" +
                "milky@eaglarts.com\n" +
                "13 Nov 2017\n" +
                "27 Nov 2017\n" +
                "AGMcrD23rzbKZmGRswTeh3Vd0Ep1\n" +
                "perfect@eaglarts.com\n" +
                "13 Nov 2017\n" +
                "15 Nov 2017\n" +
                "oyc8d59C6udSYlw12gUT9ZaU5aa2\n" +
                "aloh@gmail.com\n" +
                "13 Nov 2017\n" +
                "13 Nov 2017\n" +
                "Mg67IVInCDe6aI8hfKivGzpgtxq2\n" +
                "aloha@gmail.com\n" +
                "13 Nov 2017\n" +
                "13 Nov 2017\n" +
                "AHsHmqiDpSgwkUevDnAs8J4sYg53\n" +
                "cupofheat@gmail.com\n" +
                "12 Nov 2017\n" +
                "3 Apr 2018\n" +
                "FO19VYgiIzMiHYsLIowAdltoA313";
        String myArray[] = tempString.split("\n");
        //EditText UIDEdit = findViewById(R.id.tempUIDEdit);
        //EditText emailEdit = findViewById(R.id.tempEmailEdit);
        MaterialButton submitButton = findViewById(R.id.tempSubmitButton);
        newSplitter(myArray);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                for (int i = 0; i < uidList.size(); i ++){
                    if (uidList.size() == emailList.size()){
                        getData(emailList.get(i), uidList.get(i));
                    }else{
                        Toast.makeText(MainActivity.this, "Lists failure", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    private void getData(String email, String uid){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        User user = new User();
        mDatabase.child("users").child(uid).child("username").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String testString = snapshot.getValue(String.class);
                System.out.println("Stringgg email:" + email + ", username:" +testString + ", uid: " + uid);
                if (testString != null) {
                    if (testString.contains(".")) {
                        System.out.println("Sdad fail email: " + email + " ,username:" + testString);
                    } else {
                        uploadBoth(email, testString);
                    }
                } else{
                    System.out.println("Sdad null username,  email: " + email + " ,username:" + testString);
                }


            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    private void uploadBoth(String email, String username){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("authentication").child(username).setValue(email);
        Toast.makeText(this, "Upload Done", Toast.LENGTH_SHORT).show();
    }

    private void oldsplitter(String[] tempArray){
        emailList = new ArrayList<>();
        uidList = new ArrayList<>();
        if (((tempArray.length % 2) == 0) && (tempArray.length == 200) ){
            for (int i = 0; i < tempArray.length; i ++){
                if ((i % 2) == 0) {
                    emailList.add(tempArray[i]);
                }
                else {
                    uidList.add(tempArray[i]);
                }
            }
        }else{
            System.out.println("Sdad complete fail");
        }

    }

    private void newSplitter(String[] tempArray){
        emailList = new ArrayList<>();
        uidList = new ArrayList<>();
        if (((tempArray.length % 2) == 0) && (tempArray.length == (1000)) ){
            for (int i = 0; i < tempArray.length; i ++){
                Integer pos = i + 1;
                while ((pos - 4) > 0){
                    pos -= 4;
                }
                if (pos.equals(1)){
                    emailList.add(tempArray[i]);
                } else if (pos.equals(4)) {
                    uidList.add(tempArray[i]);
                }
            }
        }else{
            System.out.println("Sdad complete fail" + tempArray.length);
        }
    }
     */
}
