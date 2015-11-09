/* This program is used to give more accurate timeout and kill behaviour than
   can be done in Java directly. It relies on POSIX.1-2001 compliance. */
#include <sys/types.h>
#include <sys/wait.h>
#include <signal.h>
#include <stdlib.h>
#include <unistd.h>
#include <fcntl.h>
#include <stdio.h>
#include <errno.h>
#ifdef __sun
#include <sys/file.h>
#include <sys/sockio.h>
#include <stropts.h>
#define O_ASYNC FASYNC
#endif

#ifndef SIGPOLL
#define SIGPOLL SIGIO
#endif

volatile sig_atomic_t state=0;
pid_t id;
unsigned int delay;

void alarmhandler(int num) {
    /* Stack dump, wait, terminate, wait, kill. */

    char i;

    if (num==SIGPOLL) {
	/* IEEE Std 1003.1 (POSIX.1-2001) requires read to be re-entrant.
	   This part was previously disabled, since I ran into a system where
	   the read did not work, but the extended abort mechanism requires
	   actual communication here. I'm assuming whatever system that was is
	   now obsolete. */
	while((read(STDIN_FILENO, &i, 1))>0) {
	    switch(i) {
		case '.':
		    alarm(delay);		    
		    break;
		case 'q':
		    state = 1;
		    num = SIGALRM;
	    }
	}
    }

    if (num == SIGTERM) {
	kill(id, SIGTERM);
	state=2;
	alarm(10);
    }

    if (num!=SIGALRM)
	return;

    switch(state) {
	case 0:
	    kill(id, SIGQUIT);
	    state=1;
	    alarm(10);
	    break;
	case 1:
	    kill(id, SIGTERM);
	    state=2;
	    alarm(10);
	    break;
	case 2:
	    kill(id, SIGKILL);
	    state=3;
	    alarm(60);
	    break;
	case 3:
	    raise(SIGTERM);
	    raise(SIGKILL);
    }
}

int main(int argc, char *argv[]) {
    pid_t w;
    int status;
    char **argscut;
    int i;

    if (argc<2) {
	fprintf(stderr, "POSIX timeout available as %s\n", argv[0]);
	exit(0);
    }

    id = fork();
    if (id == -1) {
	perror("fork");
	exit(1);
    }

    if (id == 0) {
	argscut=malloc(sizeof(char *)*(argc-1));
	for(i=0;i<argc-2;i++)
	    argscut[i]=argv[i+2];
	argscut[i]=NULL;

	fprintf(stderr, "POSIX launcher: Spawning process %ld\n", (long) getpid());
	execvp(argv[2], argscut);
	perror("exec");
	exit(1);
    } else {
        int on=1;
	int stp=0;
	int fail=0;
	struct sigaction sa;
	sa.sa_handler=alarmhandler;
	sigemptyset(&sa.sa_mask);
	sa.sa_flags=0;

	sigaction(SIGTERM, &sa, NULL);
	sigaction(SIGALRM, &sa, NULL);
	sigaction(SIGPOLL, &sa, NULL);

	fcntl(STDIN_FILENO, F_SETOWN, getpid());
	fcntl(STDIN_FILENO, F_SETFL, O_ASYNC|O_NONBLOCK);
#ifdef __sun
	ioctl (STDIN_FILENO, SIOCSPGRP, &on);
	ioctl(STDIN_FILENO, I_SETSIG, S_RDNORM|S_INPUT);
#endif

	delay=atoi(argv[1]);

	alarm(delay);

	while(!stp) {
	    w = waitpid(id, &status, WUNTRACED | WCONTINUED);
	    if (w == -1) {
		if (errno!=EINTR) {
		    perror("waitpid");
		    exit(EXIT_FAILURE);
		}
		continue;
	    }

	    if (WIFEXITED(status)) {
		fprintf(stderr, "POSIX launcher: Exited with status %d.\n",
			WEXITSTATUS(status));
		stp=1;
	    } else if (WIFSIGNALED(status)) {
		fprintf(stderr, "POSIX launcher: Killed by signal %d.\n",
			WTERMSIG(status));
		stp=1;
		fail = 1;
	    } else if (WIFSTOPPED(status))
		fprintf(stderr, "POSIX launcher: Stopped by signal %d.\n",
			WSTOPSIG(status));
	    else if (WIFCONTINUED(status))
		fprintf(stderr, "POSIX launcher: Continued\n");

	}

	if (state > 0) {
	    fprintf(stderr, "POSIX launcher: Program aborted.\n");
	    fail = 1;
	}

	if (fail)
	    exit(EXIT_FAILURE);
	else
	    exit(EXIT_SUCCESS);
    }
}
