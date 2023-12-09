import React, { Component } from "react";
import style from "./Menu.css";

export default class Menu extends Component {
  render() {
    return (
      <ul className={style.Menu}>
        <li>
          <a href="/">Home</a>
        </li>
        <li>
          <a href="/page1/">Page 1</a>
        </li>
        <li>
          <a href="/page2/">Page 2</a>
        </li>
        <li>
          <a href="/page3/">Page 3</a>
        </li>
      </ul>
    );
  }
}
