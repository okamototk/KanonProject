■Kanon Project  

■はじめに
オープンソースでプロジェクトをガントチャートで管理できるソフトウェアとして、OpenProjがあります。
しかしながら、メンテナンスが全くされていない。バグが多い(そもそもビルドできない)などOSSとしては問題
があります。KanonはOpenProjをベースに、バグフィックスや機能拡張を行ったプロジェクト管理ソフトウェアです。

MS Projectと同じフィールでプロジェクトを管理することができます。また、Trac/Redminなどのチケットシステムとの
連携も視野に入れています。


■ビルド
■■準備
Kanonは、Mavenリポジトリにないjarを利用します。下記のコマンドによりjarをMavenリポジトリへインストールしてください。

[Windows]
> install-jar.bat

[Linux]
$ install-jar.sh

■■ビルド
下記のコマンドで全てのモジュールがビルドされます。

$ mvn install

■■パッケージ作成
下記のコマンドを実行すると、KanonProject-x.x.x.zipという名前で配布ファイルが作成されます。

$ mvn assembly:assembly

Java Web Start用のファイルを生成するには、pkgディレクトリで

$ mvn webstart:jnlp

と入力してください。target/jnlpディレクトリに署名済みのjarとjnlpファイルが作成されます。

■■実行
pkg/targetディレクトリに生成されたzipを展開して、binディレクトリの kanon.bat or kanon スリプトを実行します。


■謝辞
Kanon ProjectはSennaProjityにより開発されたOpenProjをもとに開発されています。
OpenProjについては、http://openproj.org/をご覧ください。
