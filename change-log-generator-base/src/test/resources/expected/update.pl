#!/opt/perl/bin/perl

use strict;
use warnings;

use lib '/data/lib/modules';

use HVE::Updater 'mysql_script_runner';

my $updater = HVE::Updater->new(
	stages => sub {
		my ($context, $details) = @_;

		return if $details->{db_updated};

		return [ mysql_script_runner($context, 'test.sql') ];
	},
);

$updater->start();

1;
