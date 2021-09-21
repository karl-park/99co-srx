/*
    This file is only for App's Constant
*/
import React, { Component } from "react";
import { Platform } from "react-native";

const production = "https://www.srx.com.sg";
const trainer = "https://www.srxtrainer.com";
const demo1 = 'http://demo1.srxtrainer.com';
const demo2 = 'http://demo2.srxtrainer.com';

const domain = production;

const storeURL =
  "https://itunes.apple.com/sg/app/srx-property/id881817538?mt=8";
const playStoreURL = "https://play.google.com/store/apps/details?id=sg.com.srx";

const webStoreURL = domain + "/srx/apps/srxproperty/redirect.jsp";

const ConstantList = [production, trainer, demo1, demo2];

const AppConstant = {
  DomainUrl: domain,
  AppName: "SRX Property",
  DownloadSourceURL: Platform.OS === "ios" ? storeURL : playStoreURL,
  DownloadSourceWebURL: webStoreURL
};

export { AppConstant, ConstantList };
