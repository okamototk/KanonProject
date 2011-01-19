#
# spec file for package openproj
#
Summary: OpenProj
Name: openproj
Version: @version@
Release: @rpm_revision@
License: CPAL
Group: Applications/Office
URL: http://www.openproj.com
Vendor: Projity
Packager: Laurent Chretienneau, Howard Katz
Prefix: %{_prefix}/share/openproj
BuildArchitectures: noarch
Requires: jre >= 1.5.0
Requires(post): desktop-file-utils
Requires(post): shared-mime-info
Requires(postun): desktop-file-utils
Requires(postun): shared-mime-info

%description
A desktop replacement for Microsoft Project. It is capable of sharing files with Microsoft Project and has very similar functionality (Gantt, PERT diagram, histogram, charts, reports, detailed usage), as well as tree views which aren't in MS Project.

%prep

%build

%install


%post
update-desktop-database &> /dev/null || :
update-mime-database %{_datadir}/mime &> /dev/null || :

%postun
update-desktop-database &> /dev/null || :
update-mime-database %{_datadir}/mime &> /dev/null || :

%files
/usr/share/openproj
/usr/bin/openproj
%{_datadir}/icons/openproj.png
%{_datadir}/applications/openproj.desktop
%{_datadir}/mime/packages/openproj.xml
