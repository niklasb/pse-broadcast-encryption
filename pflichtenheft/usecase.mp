input metauml;
beginfig(1);
  Actor.leiter("Übungsleiter");
  Actor.tutor("Tutor");
  Actor.student("Student");

  Usecase.loesungsabgabe("Lösungsabgabe");
  Usecase.bewertung("Bewertung");
  Usecase.punkteuebersicht("Punkteübersicht");
  Usecase.punkteuebersicht_tutor("Punkteübersicht", "(tutorspezifisch)");
  Usecase.authentifizierung("Authentifizierung");
  Usecase.ldap("Authentifizierung", "per SCC-LDAP");

  leftToRight(50)(punkteuebersicht, punkteuebersicht_tutor,
                  bewertung, loesungsabgabe);

  bottomToTop(30)(punkteuebersicht, leiter);
  bottomToTop(30)(punkteuebersicht_tutor, tutor);
  bottomToTop(30)(loesungsabgabe, student);

  bottomToTop(30)(tutor, authentifizierung);
  rightToLeft(30)(authentifizierung, ldap);

  drawObjects(punkteuebersicht, punkteuebersicht_tutor,
              bewertung, loesungsabgabe, leiter, tutor,
              student, authentifizierung, ldap);

  link(association)(leiter.s -- punkteuebersicht.n);
  link(association)(tutor.s -- punkteuebersicht_tutor.n);
  link(association)(tutor.s -- bewertung.n);
  link(association)(student.s -- loesungsabgabe.n);

  link(association)(leiter.n -- authentifizierung.sw);
  link(association)(tutor.n -- authentifizierung.s);
  link(association)(student.n -- authentifizierung.se);

  clink(inheritance)(ldap, authentifizierung);
  clink(inheritance)(punkteuebersicht_tutor,
                     punkteuebersicht);
endfig;
end
