#!/usr/bin/perl
use strict;
use feature qw(say);
use Switch;
use Getopt::Std;
use Pod::Usage;
use LWP;
use Crypt::SSLeay;    # SSL for LWP
use Term::ReadKey;    # for password reading

my $GLOBAL_SERVER     = 'api.datacite.org';
my $LOCAL_SERVER      = 'localhost:8443/mds';
my $DEFAULT_USER_NAME = 'TEST.TEST';
my $DEFAULT_USER_PW   = '12345678';
my %opts;             #Getopt::Std

sub main() {
  getopts("hlnp:su:v", \%opts) or pod2usage();
  pod2usage() if $opts{h};

  my ($resource, $method, $query, $content, $content_type);
  my $command = lc shift @ARGV or pod2usage("missing command");
  switch ($command) {
    case "metadata" {
      $resource = 'metadata';
      $content_type = 'application/xml;charset=UTF-8';
      $method = uc shift @ARGV or pod2usage("missing method");
      if ($method =~ "POST|PUT") {
        my $url = shift @ARGV;
        $query .= "?url=$url" if $url;
      } else {
        my $doi = shift @ARGV or pod2usage("missing doi");
        $query = "?doi=$doi";
      }
    }
    case "doi" {
      $resource = 'doi';
      $content_type = 'text/plain;charset=UTF-8';
      $method = uc shift @ARGV or pod2usage("missing method");
      my $doi = shift @ARGV or pod2usage("missing doi (or '-')");
      if ($doi ne "-") { 
        my $url = shift @ARGV or pod2usage("missing url");
        $content = "$doi\n$url";
      }
    }
    case "datacentre" {
      $resource = 'datacentre';
      $content_type = 'application/xml;charset=UTF-8';
      $method = uc shift @ARGV or pod2usage("missing method");
      my $symbol = shift @ARGV or pod2usage("missing symbol");
      $query = "?symbol=$symbol";
    }
    case "generic" {
      $method = uc shift @ARGV or pod2usage("missing method");
      $resource = shift @ARGV or pod2usage("missing resource");
    }  
    else { pod2usage("unknown command '$command'"); }
  }
  
  if (!$content and $method =~ "POST|PUT") {
      my @content = <>;
      $content = "@content";
      chomp $content;
  }
  
  my $user_name = $opts{u} || $DEFAULT_USER_NAME;
  my $user_pw = $opts{p} || ($opts{u} ? read_pw() : $DEFAULT_USER_PW);

  my $domain = $opts{l} ? $LOCAL_SERVER : $GLOBAL_SERVER;

  my $response_code =  do_request($method,
    "https://$domain/$resource$query",
    $user_name, $user_pw, $content, $content_type);
    
  exit $response_code;
}

sub read_pw {
  print STDERR "password: ";
  ReadMode('noecho');
  my $pw = ReadLine(0);
  chomp $pw;
  ReadMode('restore');
  return $pw;
}

sub do_request {
  my ($method, $url, $user_name, $user_pw, $content, $content_type) = @_;

  # build request
  my $headers = HTTP::Headers->new(
    Accept         => 'application/xml',
    'Content-Type' => $content_type
  );
  my $req = HTTP::Request->new(
    $method => $url,
    $headers, $content
  );
  $req->authorization_basic($user_name, $user_pw) unless $opts{n};

  # pass request to the user agent and get a response back
  my $ua = LWP::UserAgent->new;
  my $res = $ua->request($req);

  # output request/response
  if ($opts{v}) {
    say STDERR "== REQUEST ==";
    say STDERR $req->method . " " . $req->uri->as_string();
    say STDERR $req->headers_as_string();
    say STDERR shorten($req->content) if $req->content;
    say STDERR "\n== RESPONSE ==";
  }
  say STDERR $res->status_line;
  say STDERR $res->headers_as_string() if $opts{v};
  say shorten($res->content) if $res->content;
  
  return $res->code();
}


sub shorten {
  my ($txt) = @_;
  return $txt unless $opts{s};
  my @rows = split "\n", $txt;
  my $rows = scalar(@rows);
  my $size = length($txt);
  return "+++ body: $rows rows, $size chars; first 60 characters:\n+++ " . substr("@rows",0,60);
}

main();

__END__

=head1 NAME

 mds-suite

=head1 SYNOPSIS

 mds-suite [options] <command> 

 Options:
   -h          - prints this help
   -l          - use a local test server
   -n          - no credentials (only for testing)
   -s          - short output (truncate request/response body)
   -u <symbol> - username (defaults to value specified in the script)
   -v          - verbose (display complete request and response)

 Commands:
   datacentre <method> <symbol>
   doi <method> (<doi> <url> | '-')
   metadata <POST|PUT> [<url>]
   metadata <DELETE|GET> <doi>
 
   [ generic <method> <resource/params> ]
 
 The body of an http POST/PUT request is read from stdin. 
 For 'doi put/post' the request body is build from commandline params,
 unless you set '-' (=read from stdin) as doi param.  