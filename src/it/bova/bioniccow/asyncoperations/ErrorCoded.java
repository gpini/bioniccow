package it.bova.bioniccow.asyncoperations;

public interface ErrorCoded {
	int OK = -1;
	int NO_INTERNET = -100;
	int API_ERROR = -101;
	int GENERIC_EXCEPTION = -102;
	int LOGIN_ISSUE = 98;
	int TIMELINE_ERROR = 300;
}
