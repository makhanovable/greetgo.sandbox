import gulp from "gulp";
import gutil from "gulp-util";
import pug from "gulp-pug";
import rename from "gulp-rename";
import gulpReplaceAssets from "gulp-replace-assets";
import path from "path";
import webpack from "webpack";
import notifier from "node-notifier";
import AssetsPlugin from "assets-webpack-plugin";
import gulpLog from "gulplog";
import del from "del";

import moduleImporter from "sass-module-importer";
import LessPluginAutoPrefix from "less-plugin-autoprefix";
import LessNpmImportPlugin from "less-plugin-npm-import";

import fs from "fs";

import sourceMaps from "gulp-sourcemaps";
import hash from "gulp-hash";
import less from "gulp-less";
import sass from "gulp-sass";
import cleanCSS from "gulp-clean-css";
import debug from "gulp-debug";

import BrowserSync from "browser-sync";

const ser = gulp.series;
const par = gulp.parallel;
const task = gulp.task;

let isWatch = false;
task("watch:on", done => {
  isWatch = true;
  done();
});

let isStand = true;
task("stand:off", done => {
  isStand = false;
  done();
});

function outDir() {
  return isStand
    ? path.resolve(__dirname, 'build', 'public')
    : path.resolve(__dirname, 'build', 'product', 'sandbox');
}

task('clean', () => del(['build']));

task("webpack", (done) => {
  //noinspection JSUnresolvedFunction
  let options = {
    entry  : path.resolve('.', 'front', 'ts', 'boot.ts'),
    output : {
      path             : outDir(),
      publicPath       : '/',
      filename         : isStand ? '[name].js' : '[name]-[chunkhash:10].js',
      sourceMapFilename: '[name]-[chunkhash:10].js.map',
    },
    devtool: isStand ? 'cheap-module-inline-source-map' : 'source-map',
    watch  : isWatch,
    module : {
      loaders: [{
        test   : /\.ts$/,
        include: path.resolve(__dirname, 'front', 'ts'),
        loader : ['ts-loader'],
      }, {
        test: /\.(html|css)$/,
        loader : 'raw-loader',
      }],
    },
    resolve: {
      extensions: [".ts", ".js"]
    },
    plugins: [
      new webpack.NoEmitOnErrorsPlugin() // otherwise error still gives a file
    ]
  };

  if (!isStand) options.plugins.push(new webpack.optimize.UglifyJsPlugin({
    sourceMap: true,
    compress : {
      // don't show unreachable variables etc
      warnings: false,
      unsafe  : true,
      angular : true,
    }
  }));

  if (!isStand) options.plugins.push(new AssetsPlugin({
    filename: 'manifest_webpack.json',
    path    : path.resolve(__dirname, 'build'),
    processOutput(assets) {
      for (let key in assets) {
        //noinspection JSUnfilteredForInLoop
        assets[key + '.js'] = assets[key].js.slice(options.output.publicPath.length);
        //noinspection JSUnfilteredForInLoop
        delete assets[key];
      }
      return JSON.stringify(assets);
    }
  }));

  webpack(options, function (err, stats) {

    let errFormer = gutil.colors.red.bold;

    if (!err) { // no hard error
      // try to get a soft error from stats
      err = stats.toJson().errors[0];
      errFormer = gutil.colors.yellow.bold;
    }

    if (err) {
      //noinspection JSUnresolvedFunction
      notifier.notify({
        title  : 'Webpack',
        message: err
      });

      gutil.log(errFormer(err));
    } else {
      gulpLog.info(stats.toString({
        colors: true
      }));
    }

    // task never errs in watch mode, it waits and recompile
    if (!isWatch && err) {
      done(err);
    } else {
      done();
    }

  });
});

task("webpack:w", ser("watch:on", "webpack"));

task('copy:based', function () {
  return gulp.src([
    "node_modules/zone.js/dist/zone.min.js",
    "node_modules/core-js/client/shim.min.js",
    "node_modules/core-js/client/shim.min.js.map",
  ]).pipe(gulp.dest(outDir()));
});
task('copy:bootstrap-fonts', function () {
  return gulp.src("node_modules/bootstrap/dist/fonts/**/*.*")
    .pipe(gulp.dest(path.resolve(outDir(), "fonts")));
});
task('copy:jquery', function () {
  return gulp.src([
    "node_modules/jquery/dist/*.min.js",
    "node_modules/jquery/dist/*.min.map",
  ], {base: 'node_modules/jquery/dist'})
    .pipe(gulp.dest(outDir()));
});

task('copy:material-thremes', function () {
  return gulp.src([
    "node_modules/@angular/material/prebuilt-themes/indigo-pink.css",

  ]).pipe(gulp.dest(outDir()));
});


task('copy:jquery-contextMenu', par(function () {
  return gulp.src([
    "node_modules/jquery-contextmenu/dist/*.min.js",
    "node_modules/jquery-contextmenu/dist/*.min.js.map",
  ], {base: 'node_modules/jquery-contextmenu/dist'})
    .pipe(gulp.dest(outDir()));
}, function () {
  return gulp.src([
    "node_modules/jquery-contextmenu/dist/*.min.css",
    "node_modules/jquery-contextmenu/dist/*.min.css.map",
  ], {base: 'node_modules/jquery-contextmenu/dist'})
    .pipe(gulp.dest(outDir()));
}, function () {
  return gulp.src("node_modules/jquery-contextmenu/dist/font/**/*.*")
    .pipe(gulp.dest(path.resolve(outDir(), "font")));
}));
task('copy:img-svg', function () {
  return gulp.src("front/img/**/*.svg")
    .pipe(gulp.dest(path.resolve(outDir(), 'img')));
});
task('copy:img-gif', function () {
  return gulp.src("front/img/**/*.gif")
    .pipe(gulp.dest(path.resolve(outDir(), 'img')));
});
task('copy:img-png', function () {
  return gulp.src("front/img/**/*.png")
    .pipe(gulp.dest(path.resolve(outDir(), 'img')));
});

task('copy', par(
  'copy:based'
  , 'copy:bootstrap-fonts'
  , 'copy:jquery'
  , 'copy:jquery-contextMenu'
  , 'copy:material-thremes'
  , 'copy:img-svg', 'copy:img-gif', 'copy:img-png'
));

task('pug', function () {

  let p = gulp.src([
    "./front/pug/*.all.pug",
    "./front/pug/*." + (isStand ? "stand" : "product") + ".pug",
    "!./front/pug/**/_*"
  ]);

  p = p.pipe(pug({
    pretty: true,
    data  : {
      asd: "wow asd"
    }
  }));

  //noinspection ES6ModulesDependencies
  p = p.on("error", console.log);

  if (!isStand) {
    ['webpack', 'stylus', 'less']
      .map(s => path.resolve('.', 'build', 'manifest_' + s + '.json'))
      .filter(fileName => fs.existsSync(fileName))
      .forEach(fileName => {
        p = p.pipe(gulpReplaceAssets(JSON.parse(fs.readFileSync(fileName))));
      })
    ;
  }

  p = p.pipe(rename(function (path) {
    ['.stand', '.product', '.all'].forEach(e => {
      if (path.basename.endsWith(e)) {
        path.basename = path.basename.slice(0, -e.length);
      }
    });
  }));

  p = p.pipe(gulp.dest(outDir()));

  return p;
});

gulp.task('pug-prod', ser('stand:off', 'pug'));

task('less', function () {

  let p = gulp.src("front/styles/*.m.less");

  p = p.pipe(sourceMaps.init());

  let autoPrefix = new LessPluginAutoPrefix({browsers: ['last 2 versions']});
  let npmImport = new LessNpmImportPlugin({prefix: '~'});

  p = p.pipe(less({
    paths  : [
      // path.join(__dirname, 'node_modules'),
      // path.join(__dirname, 'node_modules', 'bootstrap', 'less'),
    ],
    plugins: [npmImport, autoPrefix]
  }));

  if (!isStand) {
    p = p.pipe(cleanCSS());
    p = p.pipe(hash());
  }

  p = p.pipe(isStand ? sourceMaps.write() : sourceMaps.write('.'));
  p = p.pipe(gulp.dest(outDir()));

  if (!isStand) {
    p = p.pipe(hash.manifest("manifest_less.json"));
    p = p.pipe(gulp.dest(path.resolve(__dirname, "build")));
  }

  return p;
});

task('less-prod', ser('stand:off', 'less'));

task('sass', function () {

  let p = gulp.src([
    "front/styles/*.m.scss",
    "front/styles/*.m.sass",
  ]);

  p = p.pipe(debug());

  p = p.pipe(sourceMaps.init());

  //noinspection JSUnresolvedFunction
  p = p.pipe(sass({
    importer: moduleImporter(),
  }).on('error', sass.logError));

  if (!isStand) {
    p = p.pipe(cleanCSS());
    p = p.pipe(hash());
  }

  p = p.pipe(isStand ? sourceMaps.write() : sourceMaps.write('.'));

  p = p.pipe(gulp.dest(outDir()));

  if (!isStand) {
    p = p.pipe(hash.manifest("manifest_sass.json"));
    p = p.pipe(gulp.dest(path.resolve(__dirname, "build")));
  }

  return p;
});

task('sass-prod', ser('stand:off', 'sass'));

gulp.task('build', ser('clean', 'stand:off', par('copy', 'webpack', 'less', 'sass'), 'pug'));

task('assets:w', () => {
  gulp.watch('front/pug/**/*.pug', ser('pug'));
  gulp.watch('front/styles/**/*.less', par('less', 'sass'));
  gulp.watch('front/styles/**/*.sass', ser('sass'));
  gulp.watch('front/styles/**/*.scss', ser('sass'));
  gulp.watch('front/img/**/*.svg', ser('copy:img-svg'));
  gulp.watch('front/img/**/*.gif', ser('copy:img-gif'));
  gulp.watch('front/img/**/*.png', ser('copy:img-png'));
});

task('server', () => {
  let browser = BrowserSync.create();

  browser.init({
    server: path.resolve('build', 'public')
  });

  browser.watch("build/public/**/*.*").on('change', browser.reload);
});

task('start', ser('clean',
  par('copy', 'less', 'sass', 'pug'),
  'watch:on', par('webpack', 'server', 'assets:w')
));
