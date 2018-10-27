import React from "react";

import MenuIcon from "@material-ui/icons/Menu";
import AccountCircle from "@material-ui/icons/AccountCircle";

import AppBar from "@material-ui/core/AppBar/AppBar";
import Toolbar from "@material-ui/core/Toolbar/Toolbar";
import IconButton from "@material-ui/core/IconButton/IconButton";
import Typography from "@material-ui/core/Typography/Typography";
import MenuItem from "@material-ui/core/MenuItem/MenuItem";
import Button from "@material-ui/core/Button/Button";
import Menu from "@material-ui/core/Menu/Menu";
import Drawer from "@material-ui/core/Drawer/Drawer";
import List from "@material-ui/core/List/List";
import ListItem from "@material-ui/core/ListItem/ListItem";
import ListItemIcon from "@material-ui/core/ListItemIcon/ListItemIcon";
import ListItemText from "@material-ui/core/ListItemText/ListItemText";
import Divider from "@material-ui/core/Divider/Divider";
import withStyles from "@material-ui/core/styles/withStyles";
import {Link} from "react-router-dom";

@withStyles({
    title: {
        flexGrow: 1
    },
    logo: {
        margin: "1rem"
    }
})
export default class HeaderBar extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            authorized: sessionStorage.getItem("accessToken") !== null,
            anchorElement: null,
            drawerOpen: false
        }
    }

    handleAccountMenuOpen = (event) => {
        this.setState({anchorElement: event.currentTarget});
    };

    handleAccountMenuClose = () => {
        this.setState({anchorElement: null});
    };

    handleDrawerOpen = () => {
        this.setState({drawerOpen: true});
    };

    handleDrawerClose = () => {
        this.setState({drawerOpen: false});
    };

    render() {
        const {authorized, anchorElement, drawerOpen} = this.state;
        const {classes, title} = this.props;
        const accountMenuOpen = Boolean(anchorElement);

        return (
            <div>
                <AppBar position="sticky">
                    <Toolbar>
                        <IconButton color="inherit" onClick={this.handleDrawerOpen}>
                            <MenuIcon/>
                        </IconButton>
                        <Typography component="h6" variant="h6" color="inherit" className={classes.title}>
                            {title}
                        </Typography>
                        {
                            authorized
                                ? (
                                    <div>
                                        <IconButton color="inherit" onClick={this.handleAccountMenuOpen}>
                                            <AccountCircle/>
                                        </IconButton>
                                        <Menu
                                            id="menu-appbar"
                                            anchorEl={anchorElement}
                                            anchorOrigin={{
                                                vertical: "top",
                                                horizontal: "right"
                                            }}
                                            transformOrigin={{
                                                vertical: "top",
                                                horizontal: "right"
                                            }}
                                            open={accountMenuOpen}
                                            onClose={this.handleAccountMenuClose}>
                                            <MenuItem onClick={this.handleAccountMenuClose}>Profile</MenuItem>
                                            <MenuItem onClick={this.handleAccountMenuClose}>Logout</MenuItem>
                                        </Menu>
                                    </div>
                                )
                                : <Button component={Link} to="/" color="inherit">Login</Button>
                        }
                    </Toolbar>
                </AppBar>

                <Drawer
                    anchor="left"
                    open={drawerOpen}
                    onClose={this.handleDrawerClose}>
                    <Typography component="h1" variant="h6" className={classes.logo}>Logo</Typography>
                    <Divider/>
                    <List component="nav">
                        <ListItem button>
                            <ListItemIcon>
                                <AccountCircle/>
                            </ListItemIcon>
                            <ListItemText primary="Menu Item"/>
                        </ListItem>
                    </List>
                </Drawer>
            </div>
        )
    }
}