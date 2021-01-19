const path = require('path');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');

module.exports = {
    entry: {
        'js/login.min.js': path.join(__dirname, 'src/page/login/index.tsx')
    },

    output: {
        path: path.join(__dirname, 'target/classes/frontend-scripts'),
        filename: '[name]'
    },

    resolve: {
        extensions: ['.js', '.jsx', '.ts', '.tsx', '.scss'],
    },

    module: {
        rules: [
            {
                test: /\.(js|jsx)$/,
                exclude: /node_modules/,
                use: {
                    loader: 'babel-loader',
                    options: {
                        presets: ['@babel/preset-env'],
                        plugins: ['@babel/plugin-transform-runtime']
                    }
                }
            },
            {
                test: /\.(tsx|ts)$/,
                use: 'ts-loader',
                exclude: '/node_modules/'
            },
            {
                test: /\.css$/,
                use: ['style-loader', 'css-loader']
            },
            {
                test: /\.s[ac]ss$/i,
                use: [
                    MiniCssExtractPlugin.loader,
                    'css-loader',
                    'sass-loader',
                ]
            }
        ]
    },

    plugins: [new MiniCssExtractPlugin()]
};