import React from "react";
import AddIcon from "@material-ui/icons/Add";
import Button from "@material-ui/core/Button/Button";
import withStyles from "@material-ui/core/styles/withStyles";
import Dialog from "@material-ui/core/Dialog/Dialog";
import Tabs from "@material-ui/core/Tabs/Tabs";
import Tab from "@material-ui/core/Tab/Tab";
import DialogContent from "@material-ui/core/DialogContent/DialogContent";

@withStyles({
    fab: {
        position: "absolute",
        bottom: "1rem",
        right: "1rem"
    }
})
export default class CreateFab extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            open: false,
            tab: 0
        }
    }

    handleClickOpen = () => {
        this.setState({
            open: true,
            tab: this.props.selectedCourse === undefined ? 0 : 1
        });
    };

    handleClose = () => {
        this.setState({open: false});
    };

    handleTabChange = (event, tab) => {
        this.setState({tab});
    };

    render() {
        const {open, tab} = this.state;
        const {classes} = this.props;

        return (
            <>
                <Button onClick={this.handleClickOpen} variant="fab" color="secondary" className={classes.fab}>
                    <AddIcon/>
                </Button>

                <Dialog
                    open={open}
                    onClose={this.handleClose}>
                    <Tabs
                        value={tab}
                        onChange={this.handleTabChange}>
                        <Tab label="Course"/>
                        <Tab label="Group"/>
                    </Tabs>
                    <DialogContent>
                        {
                            tab === 0 && <p>Create course</p>
                        }
                        {
                            tab === 1 && <p>Create study group</p>
                        }
                    </DialogContent>
                </Dialog>
            </>
        )
    }
}